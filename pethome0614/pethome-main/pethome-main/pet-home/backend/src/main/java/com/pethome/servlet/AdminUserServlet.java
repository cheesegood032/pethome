package com.pethome.servlet;

import com.pethome.dao.UserDAO;
import com.pethome.util.DBUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * 后台用户管理 Servlet
 * URL映射: /api/admin/user/*
 */
public class AdminUserServlet extends BaseServlet {

    private UserDAO userDAO = new UserDAO();

    protected void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String phone = req.getParameter("phone");
        int page = getIntParam(req, "page", 1);
        int pageSize = getIntParam(req, "pageSize", 10);

        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (username != null && !username.trim().isEmpty()) {
            where.append(" AND username LIKE ?");
            params.add("%" + username.trim() + "%");
        }
        if (phone != null && !phone.trim().isEmpty()) {
            where.append(" AND phone LIKE ?");
            params.add("%" + phone.trim() + "%");
        }

        String countSql = "SELECT COUNT(*) FROM user" + where;
        String dataSql = "SELECT id,username,phone,email,avatar,address,create_time,update_time FROM user"
                + where + " ORDER BY create_time DESC LIMIT ?, ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(countSql);
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            rs = ps.executeQuery();
            int total = 0;
            if (rs.next()) total = rs.getInt(1);
            rs.close(); ps.close();

            ps = conn.prepareStatement(dataSql);
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ps.setInt(params.size() + 1, (page - 1) * pageSize);
            ps.setInt(params.size() + 2, pageSize);
            rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) list.add(toMap(rs));

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("list", list);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            renderSuccess(resp, result);
        } catch (SQLException e) {
            throw new RuntimeException("查询用户列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    protected void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        Object idObj = body.get("id");
        if (idObj == null) { renderError(resp, 400, "用户ID不能为空"); return; }
        long userId = ((Number) idObj).longValue();
        body.remove("id");
        body.remove("password");
        body.remove("username");
        body.remove("create_time");
        userDAO.updateInfo(userId, body);
        renderSuccess(resp, null);
    }

    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        Object idObj = body.get("id");
        if (idObj == null) idObj = body.get("user_id");
        if (idObj == null) { renderError(resp, 400, "用户ID不能为空"); return; }
        long userId = ((Number) idObj).longValue();

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            // 级联删除关联数据
            executeInTx(conn, "DELETE FROM foster_order_pet WHERE foster_order_id IN (SELECT id FROM foster_order WHERE user_id=?)", userId);
            executeInTx(conn, "DELETE FROM foster_order WHERE user_id=?", userId);
            executeInTx(conn, "DELETE FROM order_item WHERE order_id IN (SELECT id FROM product_order WHERE user_id=?)", userId);
            executeInTx(conn, "DELETE FROM product_order WHERE user_id=?", userId);
            executeInTx(conn, "DELETE FROM cart WHERE user_id=?", userId);
            executeInTx(conn, "DELETE FROM pet WHERE user_id=?", userId);
            executeInTx(conn, "DELETE FROM user WHERE id=?", userId);
            conn.commit();
            renderSuccess(resp, null);
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            e.printStackTrace();
            renderError(resp, 500, "删除用户失败: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (Exception e) { e.printStackTrace(); }
                DBUtil.close(conn);
            }
        }
    }

    protected void profile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getLongParam(req, "user_id", 0);
        if (userId <= 0) { renderError(resp, 400, "用户ID不能为空"); return; }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            Map<String, Object> data = new LinkedHashMap<>();

            // 基本信息
            Map<String, Object> user = userDAO.findById(userId);
            if (user == null) { renderError(resp, 404, "用户不存在"); return; }
            user.remove("password");
            data.put("user", user);

            // 宠物列表
            ps = conn.prepareStatement("SELECT * FROM pet WHERE user_id=? ORDER BY create_time DESC");
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            List<Map<String, Object>> pets = new ArrayList<>();
            while (rs.next()) pets.add(toMap(rs));
            data.put("pets", pets);
            rs.close(); ps.close();

            // 最近10条商城订单
            ps = conn.prepareStatement("SELECT id,order_no,total_price,status,delivery_method,create_time FROM product_order WHERE user_id=? ORDER BY create_time DESC LIMIT 10");
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            List<Map<String, Object>> orders = new ArrayList<>();
            while (rs.next()) orders.add(toMap(rs));
            data.put("orders", orders);
            rs.close(); ps.close();

            // 最近10条寄养订单
            ps = conn.prepareStatement("SELECT fo.id,fo.order_no,fo.total_price,fo.status,fo.start_date,fo.end_date,fo.total_days,fp.name AS package_name FROM foster_order fo LEFT JOIN foster_package fp ON fo.package_id=fp.id WHERE fo.user_id=? ORDER BY fo.create_time DESC LIMIT 10");
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            List<Map<String, Object>> fosters = new ArrayList<>();
            while (rs.next()) fosters.add(toMap(rs));
            data.put("fosterOrders", fosters);

            renderSuccess(resp, data);
        } catch (SQLException e) {
            throw new RuntimeException("查询用户画像失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    private void executeInTx(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    private Map<String, Object> toMap(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) map.put(meta.getColumnLabel(i), rs.getObject(i));
        return map;
    }
}
