package com.pethome.servlet;

import com.alibaba.fastjson.JSONArray;
import com.pethome.dao.FosterDAO;
import com.pethome.util.DBUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * 寄养模块 Servlet（用户端）
 * URL映射: /api/foster/*
 * 子路径: package/list, package/all, package/detail, order/list, order/create, order/cancel, order/pay, order/detail
 */
public class FosterServlet extends BaseServlet {

    private FosterDAO fosterDAO = new FosterDAO();

    private long getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("loginUser");
        return ((Number) user.get("id")).longValue();
    }

    @Override
    protected void service(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
            throws jakarta.servlet.ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            renderError(resp, 404, "接口不存在");
            return;
        }

        // 解析路径: /package/list -> packageList, /order/create -> orderCreate
        String path = pathInfo.substring(1); // remove leading /
        String methodName;
        if (path.startsWith("package/")) {
            String sub = path.substring("package/".length());
            methodName = "package" + sub.substring(0, 1).toUpperCase() + sub.substring(1);
        } else if (path.startsWith("order/")) {
            String sub = path.substring("order/".length());
            methodName = "order" + sub.substring(0, 1).toUpperCase() + sub.substring(1);
        } else {
            renderError(resp, 404, "接口不存在: " + path);
            return;
        }

        try {
            java.lang.reflect.Method method = this.getClass().getDeclaredMethod(
                    methodName, jakarta.servlet.http.HttpServletRequest.class, jakarta.servlet.http.HttpServletResponse.class);
            method.setAccessible(true);
            method.invoke(this, req, resp);
        } catch (NoSuchMethodException e) {
            renderError(resp, 404, "接口不存在: " + methodName);
        } catch (Exception e) {
            e.printStackTrace();
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            renderError(resp, 500, "服务器内部错误: " + cause.getMessage());
        }
    }

    /* =========== 套餐接口 =========== */

    protected void packageList(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws IOException {
        renderSuccess(resp, fosterDAO.findAllPackages());
    }

    protected void packageAll(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws IOException {
        renderSuccess(resp, fosterDAO.findAllPackages());
    }

    protected void packageDetail(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws IOException {
        long id = getLongParam(req, "id", 0);
        if (id <= 0) { renderError(resp, 400, "套餐ID不能为空"); return; }
        Map<String, Object> pkg = fosterDAO.findPackageById(id);
        if (pkg == null) { renderError(resp, 404, "套餐不存在"); return; }
        renderSuccess(resp, pkg);
    }

    /* =========== 订单接口 =========== */

    protected void orderList(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        String statusStr = req.getParameter("status");
        Integer status = null;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try { status = Integer.parseInt(statusStr); } catch (NumberFormatException ignored) {}
        }
        int page = getIntParam(req, "page", 1);
        int pageSize = getIntParam(req, "pageSize", 10);
        Map<String, Object> result = fosterDAO.findOrdersByUserId(userId, status, page, pageSize);
        renderSuccess(resp, result);
    }

    protected void orderAll(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws IOException {
        orderList(req, resp);
    }

    protected void orderDetail(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws IOException {
        long id = getLongParam(req, "id", 0);
        if (id <= 0) { renderError(resp, 400, "订单ID不能为空"); return; }
        Map<String, Object> order = fosterDAO.findOrderById(id);
        if (order == null) { renderError(resp, 404, "订单不存在"); return; }
        renderSuccess(resp, order);
    }

    protected void orderCreate(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        Map<String, Object> body = parseBody(req);

        // 解析宠物 ID 列表
        List<Long> petIds = new ArrayList<>();
        Object petIdsObj = body.get("pet_ids");
        if (petIdsObj == null) petIdsObj = body.get("petIds");
        if (petIdsObj instanceof List) {
            for (Object o : (List<?>) petIdsObj) petIds.add(((Number) o).longValue());
        } else if (petIdsObj instanceof JSONArray) {
            JSONArray arr = (JSONArray) petIdsObj;
            for (int i = 0; i < arr.size(); i++) petIds.add(arr.getLong(i));
        }
        if (petIds.isEmpty()) { renderError(resp, 400, "请选择寄养的宠物"); return; }

        Object pkgIdObj = body.get("package_id");
        if (pkgIdObj == null) pkgIdObj = body.get("packageId");
        if (pkgIdObj == null) { renderError(resp, 400, "请选择寄养套餐"); return; }
        long packageId = ((Number) pkgIdObj).longValue();

        String startDate = (String) body.get("start_date");
        if (startDate == null) startDate = (String) body.get("startDate");
        String endDate = (String) body.get("end_date");
        if (endDate == null) endDate = (String) body.get("endDate");

        if (startDate == null || endDate == null) { renderError(resp, 400, "请选择日期范围"); return; }

        Object totalDaysObj = body.get("total_days");
        if (totalDaysObj == null) totalDaysObj = body.get("totalDays");
        int totalDays = totalDaysObj != null ? ((Number) totalDaysObj).intValue() : 1;

        Object totalPriceObj = body.get("total_price");
        if (totalPriceObj == null) totalPriceObj = body.get("totalPrice");
        double totalPrice = totalPriceObj != null ? ((Number) totalPriceObj).doubleValue() : 0;

        String remark = (String) body.getOrDefault("remark", "");

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            String orderNo = fosterDAO.generateOrderNo();
            long orderId = fosterDAO.insertFosterOrder(conn, orderNo, userId, packageId,
                    startDate, endDate, totalDays, totalPrice, remark);
            for (Long petId : petIds) {
                fosterDAO.insertFosterOrderPet(conn, orderId, petId);
            }
            conn.commit();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("orderId", orderId);
            data.put("orderNo", orderNo);
            renderSuccess(resp, data);
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            e.printStackTrace();
            renderError(resp, 500, "创建寄养订单失败: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (Exception e) { e.printStackTrace(); }
                DBUtil.close(conn);
            }
        }
    }

    protected void orderCancel(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        long orderId = getLongParam(req, "orderId", 0);
        if (orderId <= 0) {
            Map<String, Object> body = parseBody(req);
            Object idObj = body.get("orderId");
            if (idObj == null) idObj = body.get("order_id");
            if (idObj != null) orderId = ((Number) idObj).longValue();
        }
        if (orderId <= 0) { renderError(resp, 400, "订单ID不能为空"); return; }
        int rows = fosterDAO.cancelOrder(orderId, userId);
        if (rows > 0) renderSuccess(resp, null);
        else renderError(resp, 400, "取消失败，仅待审核的订单可取消");
    }

    protected void orderPay(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        long orderId = getLongParam(req, "orderId", 0);
        if (orderId <= 0) {
            Map<String, Object> body = parseBody(req);
            Object idObj = body.get("orderId");
            if (idObj == null) idObj = body.get("order_id");
            if (idObj != null) orderId = ((Number) idObj).longValue();
        }
        if (orderId <= 0) { renderError(resp, 400, "订单ID不能为空"); return; }
        int rows = fosterDAO.payOrder(orderId, userId);
        if (rows > 0) renderSuccess(resp, null);
        else renderError(resp, 400, "支付失败，订单状态不正确");
    }
}
