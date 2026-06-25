package com.pethome.servlet;

import com.pethome.dao.CartDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 购物车模块 Servlet
 * URL映射: /api/cart/*
 */
public class CartServlet extends BaseServlet {

    private CartDAO cartDAO = new CartDAO();

    private long getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("loginUser");
        return ((Number) user.get("id")).longValue();
    }

    /**
     * POST /api/cart/add — 加入购物车
     * // A：负责实现将商品加入购物车的功能，如果购物车中已有同款商品则累加数量，否则插入新记录
     */
    protected void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        Map<String, Object> body = parseBody(req);

        Object pidObj = body.get("product_id");
        if (pidObj == null) pidObj = body.get("productId");
        if (pidObj == null) {
            renderError(resp, 400, "商品ID不能为空");
            return;
        }
        long productId = ((Number) pidObj).longValue();

        Object qtyObj = body.get("quantity");
        int quantity = qtyObj != null ? ((Number) qtyObj).intValue() : 1;
        String spec = (String) body.get("spec");
        if (spec == null) spec = "";

        // 若已有记录则累加
        Map<String, Object> existing = cartDAO.findByUserProductSpec(userId, productId, spec);
        if (existing != null) {
            cartDAO.addQuantity(((Number) existing.get("id")).longValue(), quantity);
        } else {
            cartDAO.insert(userId, productId, quantity, spec);
        }
        renderSuccess(resp, null);
    }

    /**
     * GET /api/cart/list — 购物车列表
     * // A：负责实现获取当前用户的购物车列表数据，供前端展示购物车明细
     */
    protected void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        List<Map<String, Object>> list = cartDAO.findListByUserId(userId);
        renderSuccess(resp, list);
    }

    /**
     * POST /api/cart/update — 修改数量
     * // A：负责实现更新购物车中商品数量的功能，支持前端增加或减少购物车商品数量
     */
    protected void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        Object cidObj = body.get("cart_id");
        if (cidObj == null) cidObj = body.get("cartId");
        if (cidObj == null) cidObj = body.get("id");
        if (cidObj == null) {
            renderError(resp, 400, "购物车ID不能为空");
            return;
        }
        long cartId = ((Number) cidObj).longValue();
        Object qtyObj = body.get("quantity");
        if (qtyObj == null) {
            renderError(resp, 400, "数量不能为空");
            return;
        }
        int quantity = ((Number) qtyObj).intValue();
        if (quantity <= 0) {
            renderError(resp, 400, "数量必须大于0");
            return;
        }
        cartDAO.updateQuantity(cartId, quantity);
        renderSuccess(resp, null);
    }

    /**
     * POST /api/cart/delete — 删除单条
     * // A：负责实现从购物车中删除指定单条商品记录的功能
     */
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        Map<String, Object> body = parseBody(req);
        Object cidObj = body.get("cart_id");
        if (cidObj == null) cidObj = body.get("cartId");
        if (cidObj == null) cidObj = body.get("id");
        if (cidObj == null) {
            renderError(resp, 400, "购物车ID不能为空");
            return;
        }
        long cartId = ((Number) cidObj).longValue();
        cartDAO.delete(cartId, userId);
        renderSuccess(resp, null);
    }

    /**
     * POST /api/cart/remove — 删除（兼容前端命名）
     */
    protected void remove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        String cartIdStr = req.getParameter("cartId");
        if (cartIdStr == null || cartIdStr.trim().isEmpty()) {
            // 尝试从 body 读取
            Map<String, Object> body = parseBody(req);
            Object cidObj = body.get("cartId");
            if (cidObj == null) cidObj = body.get("cart_id");
            if (cidObj != null) {
                cartIdStr = cidObj.toString();
            }
        }
        if (cartIdStr == null || cartIdStr.trim().isEmpty()) {
            renderError(resp, 400, "购物车ID不能为空");
            return;
        }
        long cartId = Long.parseLong(cartIdStr.trim());
        cartDAO.delete(cartId, userId);
        renderSuccess(resp, null);
    }

    /**
     * POST /api/cart/clear — 清空购物车
     * // A：负责实现清空当前用户购物车所有商品记录的功能，常用于订单结算后
     */
    protected void clear(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        cartDAO.clearByUserId(userId);
        renderSuccess(resp, null);
    }
}
