package com.pethome.servlet;

import com.alibaba.fastjson.JSONArray;
import com.pethome.dao.CartDAO;
import com.pethome.dao.OrderDAO;
import com.pethome.dao.ProductDAO;
import com.pethome.util.DBUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商城订单模块 Servlet
 * URL映射: /api/order/*
 */
public class OrderServlet extends BaseServlet {

    private OrderDAO orderDAO = new OrderDAO();
    private CartDAO cartDAO = new CartDAO();
    private ProductDAO productDAO = new ProductDAO();

    private long getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("loginUser");
        return ((Number) user.get("id")).longValue();
    }

    /**
     * POST /api/order/create — 创建订单（事务）
     * // 成员A：负责实现购物车结算并生成订单的功能，包含扣减库存、生成订单表、订单明细表以及清空购物车，通过数据库事务保证数据一致性
     */
    protected void create(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        Map<String, Object> body = parseBody(req);

        // 解析 cart_ids
        List<Long> cartIds = new ArrayList<>();
        Object cartIdsObj = body.get("cart_ids");
        if (cartIdsObj == null) cartIdsObj = body.get("cartIds");
        if (cartIdsObj instanceof List) {
            for (Object o : (List<?>) cartIdsObj) {
                cartIds.add(((Number) o).longValue());
            }
        } else if (cartIdsObj instanceof JSONArray) {
            JSONArray arr = (JSONArray) cartIdsObj;
            for (int i = 0; i < arr.size(); i++) {
                cartIds.add(arr.getLong(i));
            }
        }

        if (cartIds.isEmpty()) {
            renderError(resp, 400, "请选择要结算的商品");
            return;
        }

        // 配送方式
        Object dmObj = body.get("delivery_method");
        if (dmObj == null) dmObj = body.get("deliveryMethod");
        int deliveryMethod = dmObj != null ? ((Number) dmObj).intValue() : 1;

        Long storeId = null;
        Object storeObj = body.get("store_id");
        if (storeObj == null) storeObj = body.get("storeId");
        if (storeObj != null) {
            storeId = ((Number) storeObj).longValue();
        }

        String receiverName = (String) body.get("receiver_name");
        if (receiverName == null) receiverName = (String) body.get("receiverName");
        String receiverPhone = (String) body.get("receiver_phone");
        if (receiverPhone == null) receiverPhone = (String) body.get("receiverPhone");
        String receiverAddress = (String) body.get("receiver_address");
        if (receiverAddress == null) receiverAddress = (String) body.get("receiverAddress");
        String remark = (String) body.get("remark");

        // 查询购物车商品
        List<Map<String, Object>> cartItems = cartDAO.findByIdsAndUserId(cartIds, userId);
        if (cartItems.isEmpty()) {
            renderError(resp, 400, "购物车中没有选中的商品");
            return;
        }

        // 开启事务
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 校验库存并扣减
            double totalPrice = 0;
            for (Map<String, Object> item : cartItems) {
                int quantity = ((Number) item.get("quantity")).intValue();
                int stock = ((Number) item.get("stock")).intValue();
                String productName = (String) item.get("product_name");
                long productId = ((Number) item.get("product_id")).longValue();

                if (stock < quantity) {
                    conn.rollback();
                    renderError(resp, 400, "商品「" + productName + "」库存不足，当前库存: " + stock);
                    return;
                }

                // 扣减库存（乐观锁）
                int rows = productDAO.deductStock(conn, productId, quantity);
                if (rows == 0) {
                    conn.rollback();
                    renderError(resp, 400, "商品「" + productName + "」库存不足，请刷新重试");
                    return;
                }

                double price = ((Number) item.get("price")).doubleValue();
                totalPrice += price * quantity;
            }

            // 2. 生成订单号
            String orderNo = orderDAO.generateOrderNo();

            // 3. 写入 product_order
            long orderId = orderDAO.insertOrder(conn, orderNo, userId, totalPrice,
                    deliveryMethod, storeId, receiverName, receiverPhone,
                    receiverAddress, remark);

            // 4. 写入 order_item
            for (Map<String, Object> item : cartItems) {
                long productId = ((Number) item.get("product_id")).longValue();
                String productName = (String) item.get("product_name");
                double price = ((Number) item.get("price")).doubleValue();
                int quantity = ((Number) item.get("quantity")).intValue();
                String spec = (String) item.get("spec");
                orderDAO.insertOrderItem(conn, orderId, productId,
                        productName, price, quantity, spec);
            }

            // 5. 删除已结算的购物车记录
            cartDAO.deleteByIds(conn, cartIds, userId);

            // 提交事务
            conn.commit();

            Map<String, Object> data = new java.util.LinkedHashMap<>();
            data.put("orderId", orderId);
            data.put("orderNo", orderNo);
            data.put("totalPrice", totalPrice);
            renderSuccess(resp, data);

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            renderError(resp, 500, "创建订单失败: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (Exception e) { e.printStackTrace(); }
                DBUtil.close(conn);
            }
        }
    }

    /**
     * GET /api/order/list — 我的订单列表
     * // 成员A：负责实现用户订单列表的分页查询与状态过滤功能
     */
    protected void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        String statusStr = req.getParameter("status");
        Integer status = null;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try { status = Integer.parseInt(statusStr); } catch (NumberFormatException ignored) {}
        }
        int page = getIntParam(req, "page", 1);
        int pageSize = getIntParam(req, "pageSize", 10);

        Map<String, Object> result = orderDAO.findListByUserId(userId, status, page, pageSize);
        renderSuccess(resp, result);
    }

    /**
     * GET /api/order/detail — 订单详情
     * // 成员A：负责实现获取指定订单详细信息的功能，用于前端订单详情页的数据展示
     */
    protected void detail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String oidStr = req.getParameter("orderId");
        if (oidStr == null) oidStr = req.getParameter("id");
        if (oidStr == null || oidStr.trim().isEmpty()) {
            renderError(resp, 400, "订单ID不能为空");
            return;
        }
        long orderId = Long.parseLong(oidStr.trim());
        Map<String, Object> order = orderDAO.findById(orderId);
        if (order == null) {
            renderError(resp, 404, "订单不存在");
            return;
        }
        renderSuccess(resp, order);
    }

    /**
     * POST /api/order/pay — 模拟支付 (1→2)
     * // 成员A：负责实现订单支付模拟功能，将订单状态从待支付更新为已支付
     */
    protected void pay(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        long orderId = getOrderIdFromRequest(req);
        if (orderId <= 0) {
            renderError(resp, 400, "订单ID不能为空");
            return;
        }
        int rows = orderDAO.pay(orderId, userId);
        if (rows > 0) {
            renderSuccess(resp, null);
        } else {
            renderError(resp, 400, "支付失败，订单状态不正确或不属于当前用户");
        }
    }

    /**
     * POST /api/order/complete — 用户确认完成 (3→4)
     * // 成员A：负责实现订单确认收货的功能，将订单状态从待收货更新为已完成
     */
    protected void complete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        long orderId = getOrderIdFromRequest(req);
        if (orderId <= 0) {
            renderError(resp, 400, "订单ID不能为空");
            return;
        }
        int rows = orderDAO.complete(orderId, userId);
        if (rows > 0) {
            renderSuccess(resp, null);
        } else {
            renderError(resp, 400, "确认收货失败，订单状态不正确");
        }
    }

    /**
     * POST /api/order/receive — 确认收货（兼容前端命名）
     */
    protected void receive(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        complete(req, resp);
    }

    /**
     * POST /api/order/cancel — 取消订单 (1→9)
     * // 成员A：负责实现未支付订单的取消功能，更新状态为已取消
     */
    protected void cancel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        long orderId = getOrderIdFromRequest(req);
        if (orderId <= 0) {
            renderError(resp, 400, "订单ID不能为空");
            return;
        }
        int rows = orderDAO.cancel(orderId, userId);
        if (rows > 0) {
            renderSuccess(resp, null);
        } else {
            renderError(resp, 400, "取消失败，仅未支付的订单可以取消");
        }
    }

    /**
     * 从请求中获取订单ID（兼容 query param 和 body）
     */
    private long getOrderIdFromRequest(HttpServletRequest req) throws IOException {
        // 先从 query param 取
        String oidStr = req.getParameter("orderId");
        if (oidStr == null) oidStr = req.getParameter("order_id");
        if (oidStr == null) oidStr = req.getParameter("id");
        if (oidStr != null && !oidStr.trim().isEmpty()) {
            return Long.parseLong(oidStr.trim());
        }
        // 从 body 取
        try {
            Map<String, Object> body = parseBody(req);
            Object oid = body.get("orderId");
            if (oid == null) oid = body.get("order_id");
            if (oid == null) oid = body.get("id");
            if (oid != null) return ((Number) oid).longValue();
        } catch (Exception ignored) {}
        return 0;
    }
}
