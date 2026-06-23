package com.pethome.servlet;

import com.pethome.dao.FosterDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

/**
 * 后台寄养订单管理 Servlet
 * URL映射: /api/admin/foster/*
 */
public class AdminFosterServlet extends BaseServlet {

    private FosterDAO fosterDAO = new FosterDAO();

    protected void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String statusStr = req.getParameter("status");
        String startDateBegin = req.getParameter("start_date_begin");
        String startDateEnd = req.getParameter("start_date_end");
        int page = getIntParam(req, "page", 1);
        int pageSize = getIntParam(req, "pageSize", 10);
        Integer status = null;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try { status = Integer.parseInt(statusStr.trim()); } catch (NumberFormatException ignored) {}
        }
        Map<String, Object> result = fosterDAO.findOrdersAdmin(username, status, startDateBegin, startDateEnd, page, pageSize);
        renderSuccess(resp, result);
    }

    protected void approve(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        long orderId = getIdFromBody(body);
        if (orderId <= 0) { renderError(resp, 400, "订单ID不能为空"); return; }
        int rows = fosterDAO.approve(orderId);
        if (rows > 0) renderSuccess(resp, null);
        else renderError(resp, 400, "审核失败，订单状态出错");
    }

    protected void reject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        long orderId = getIdFromBody(body);
        String reason = (String) body.get("reject_reason");
        if (reason == null) reason = (String) body.get("rejectReason");
        if (orderId <= 0) { renderError(resp, 400, "订单ID不能为空"); return; }
        int rows = fosterDAO.reject(orderId, reason != null ? reason : "");
        if (rows > 0) renderSuccess(resp, null);
        else renderError(resp, 400, "驳回失败，订单状态出错");
    }

    protected void checkin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        long orderId = getIdFromBody(body);
        if (orderId <= 0) { renderError(resp, 400, "订单ID不能为空"); return; }
        int rows = fosterDAO.checkin(orderId);
        if (rows > 0) renderSuccess(resp, null);
        else renderError(resp, 400, "入住操作失败，订单状态出错");
    }

    protected void complete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        long orderId = getIdFromBody(body);
        if (orderId <= 0) { renderError(resp, 400, "订单ID不能为空"); return; }
        int rows = fosterDAO.completeFoster(orderId);
        if (rows > 0) renderSuccess(resp, null);
        else renderError(resp, 400, "完成操作失败，订单状态出错");
    }

    protected void monthly(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int year = getIntParam(req, "year", Calendar.getInstance().get(Calendar.YEAR));
        renderSuccess(resp, fosterDAO.monthlyStats(year));
    }

    private long getIdFromBody(Map<String, Object> body) {
        Object idObj = body.get("order_id");
        if (idObj == null) idObj = body.get("orderId");
        if (idObj == null) idObj = body.get("id");
        if (idObj == null) return 0;
        return ((Number) idObj).longValue();
    }
}
