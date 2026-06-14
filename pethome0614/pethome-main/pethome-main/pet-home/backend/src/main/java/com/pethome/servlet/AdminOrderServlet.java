package com.pethome.servlet;

import com.pethome.dao.OrderDAO;
import com.pethome.util.DBUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * 后台商城订单管理 Servlet
 * URL映射: /api/admin/order/*
 */
public class AdminOrderServlet extends BaseServlet {

    private OrderDAO orderDAO = new OrderDAO();

    /**
     * GET /api/admin/order/list
     * 多条件查询（order_no/username/status/delivery_method，分页）
     * JOIN user 表返回用户名
     */
    protected void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderNo = req.getParameter("order_no");
        String username = req.getParameter("username");
        String statusStr = req.getParameter("status");
        String deliveryMethodStr = req.getParameter("delivery_method");
        int page = getIntParam(req, "page", 1);
        int pageSize = getIntParam(req, "pageSize", 10);

        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (orderNo != null && !orderNo.trim().isEmpty()) {
            where.append(" AND o.order_no LIKE ?");
            params.add("%" + orderNo.trim() + "%");
        }
        if (username != null && !username.trim().isEmpty()) {
            where.append(" AND u.username LIKE ?");
            params.add("%" + username.trim() + "%");
        }
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            where.append(" AND o.status = ?");
            params.add(Integer.parseInt(statusStr.trim()));
        }
        if (deliveryMethodStr != null && !deliveryMethodStr.trim().isEmpty()) {
            where.append(" AND o.delivery_method = ?");
            params.add(Integer.parseInt(deliveryMethodStr.trim()));
        }

        String countSql = "SELECT COUNT(*) FROM product_order o LEFT JOIN user u ON o.user_id = u.id" + where;
        String dataSql = "SELECT o.*, u.username FROM product_order o LEFT JOIN user u ON o.user_id = u.id"
                + where + " ORDER BY o.create_time DESC LIMIT ?, ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            
            // Count
            ps = conn.prepareStatement(countSql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            rs = ps.executeQuery();
            int total = 0;
            if (rs.next()) {
                total = rs.getInt(1);
            }
            rs.close();
            ps.close();

            // Data
            ps = conn.prepareStatement(dataSql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.setInt(params.size() + 1, (page - 1) * pageSize);
            ps.setInt(params.size() + 2, pageSize);
            rs = ps.executeQuery();

            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                list.add(toMap(rs));
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("list", list);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            renderSuccess(resp, result);
        } catch (SQLException e) {
            throw new RuntimeException("查询订单列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * POST /api/admin/order/ship
     * 发货操作（order_id，送货订单 status 2→3，记录 ship_time）
     */
    protected void ship(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        long orderId = getIdFromBody(body);
        if (orderId <= 0) {
            renderError(resp, 400, "订单ID不能为空");
            return;
        }

        String sql = "UPDATE product_order SET status = 3, ship_time = NOW() WHERE id = ? AND status = 2 AND delivery_method = 1";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                renderSuccess(resp, null);
            } else {
                renderError(resp, 400, "操作失败，订单状态不正确或不是送货订单");
            }
        } catch (SQLException e) {
            throw new RuntimeException("发货操作失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * POST /api/admin/order/ready
     * 备货完成（order_id，自提订单 status 2→3，记录 ship_time）
     */
    protected void ready(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        long orderId = getIdFromBody(body);
        if (orderId <= 0) {
            renderError(resp, 400, "订单ID不能为空");
            return;
        }

        String sql = "UPDATE product_order SET status = 3, ship_time = NOW() WHERE id = ? AND status = 2 AND delivery_method = 2";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                renderSuccess(resp, null);
            } else {
                renderError(resp, 400, "操作失败，订单状态不正确或不是自提订单");
            }
        } catch (SQLException e) {
            throw new RuntimeException("备货操作失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    private long getIdFromBody(Map<String, Object> body) {
        Object idObj = body.get("order_id");
        if (idObj == null) idObj = body.get("orderId");
        if (idObj == null) idObj = body.get("id");
        if (idObj == null) return 0;
        return ((Number) idObj).longValue();
    }

    private Map<String, Object> toMap(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            map.put(meta.getColumnLabel(i), rs.getObject(i));
        }
        return map;
    }
}
