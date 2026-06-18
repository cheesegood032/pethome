package com.pethome.dao;

import com.pethome.util.DBUtil;

import java.sql.*;
import java.util.*;

/**
 * 管理员数据访问对象
 */
public class AdminDAO {

    /**
     * 根据用户名查询管理员
     */
    public Map<String, Object> findByUsername(String username) {
        String sql = "SELECT * FROM admin WHERE username = ?";
        return queryOne(sql, username);
    }

    /**
     * 根据手机号查询管理员
     */
    public Map<String, Object> findByPhone(String phone) {
        String sql = "SELECT * FROM admin WHERE phone = ?";
        return queryOne(sql, phone);
    }

    private Map<String, Object> queryOne(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return toMap(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询管理员失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    private Map<String, Object> toMap(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            map.put(meta.getColumnLabel(i), rs.getObject(i));
        }
        return map;
    }

    /**
     * 获取 Dashboard 统计数据
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            
            // 商品总数 (status = 1)
            ps = conn.prepareStatement("SELECT COUNT(*) FROM product WHERE status = 1");
            rs = ps.executeQuery();
            if (rs.next()) stats.put("productCount", rs.getInt(1));
            rs.close(); ps.close();
            
            // 订单总数
            ps = conn.prepareStatement("SELECT COUNT(*) FROM product_order");
            rs = ps.executeQuery();
            if (rs.next()) stats.put("orderCount", rs.getInt(1));
            rs.close(); ps.close();
            
            // 用户总数
            ps = conn.prepareStatement("SELECT COUNT(*) FROM user");
            rs = ps.executeQuery();
            if (rs.next()) stats.put("userCount", rs.getInt(1));
            rs.close(); ps.close();
            
            // 寄养订单数
            ps = conn.prepareStatement("SELECT COUNT(*) FROM foster_order");
            rs = ps.executeQuery();
            if (rs.next()) stats.put("fosterCount", rs.getInt(1));
            rs.close(); ps.close();
            
            // 待处理订单 (status = 1 或 2，按时间倒序)
            ps = conn.prepareStatement("SELECT o.id, o.order_no as orderNo, u.username, o.total_price as totalPrice, o.status FROM product_order o LEFT JOIN user u ON o.user_id = u.id WHERE o.status IN (1, 2) ORDER BY o.create_time DESC LIMIT 10");
            rs = ps.executeQuery();
            List<Map<String, Object>> pendingOrders = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("id", rs.getLong("id"));
                order.put("orderNo", rs.getString("orderNo"));
                order.put("username", rs.getString("username"));
                order.put("totalPrice", rs.getDouble("totalPrice"));
                order.put("status", rs.getInt("status"));
                pendingOrders.add(order);
            }
            stats.put("pendingOrders", pendingOrders);
            
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException("获取统计数据失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }
}
