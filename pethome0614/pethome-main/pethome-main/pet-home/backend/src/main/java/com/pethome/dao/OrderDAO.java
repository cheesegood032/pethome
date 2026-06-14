package com.pethome.dao;

import com.pethome.util.DBUtil;

import java.sql.*;
import java.util.*;

/**
 * 商城订单数据访问对象
 */
public class OrderDAO {

    /**
     * 生成订单号（时间戳+4位随机数）
     */
    public String generateOrderNo() {
        long ts = System.currentTimeMillis();
        int rand = (int) (Math.random() * 9000) + 1000;
        return "PH" + ts + rand;
    }

    /**
     * 创建订单（在事务中调用，传入连接）
     * 返回生成的订单 ID
     */
    public long insertOrder(Connection conn, String orderNo, long userId,
                            double totalPrice, int deliveryMethod,
                            Long storeId, String receiverName,
                            String receiverPhone, String receiverAddress,
                            String remark) throws SQLException {
        String sql = "INSERT INTO product_order(order_no, user_id, total_price, delivery_method, "
                + "store_id, status, receiver_name, receiver_phone, receiver_address, "
                + "remark, create_time) "
                + "VALUES(?,?,?,?,?,1,?,?,?,?,NOW())";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, orderNo);
            ps.setLong(2, userId);
            ps.setDouble(3, totalPrice);
            ps.setInt(4, deliveryMethod);
            if (storeId != null) {
                ps.setLong(5, storeId);
            } else {
                ps.setNull(5, Types.BIGINT);
            }
            ps.setString(6, receiverName);
            ps.setString(7, receiverPhone);
            ps.setString(8, receiverAddress);
            ps.setString(9, remark);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getLong(1) : -1;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * 插入订单项（在事务中调用）
     */
    public void insertOrderItem(Connection conn, long orderId, long productId,
                                String productName, double price,
                                int quantity, String spec) throws SQLException {
        String sql = "INSERT INTO order_item(order_id, product_id, product_name, "
                + "price, quantity, spec) VALUES(?,?,?,?,?,?)";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            ps.setLong(2, productId);
            ps.setString(3, productName);
            ps.setDouble(4, price);
            ps.setInt(5, quantity);
            ps.setString(6, spec != null ? spec : "");
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * 我的订单列表（支持 status 筛选，分页）
     */
    public Map<String, Object> findListByUserId(long userId, Integer status,
                                                 int page, int pageSize) {
        StringBuilder where = new StringBuilder(" WHERE o.user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && status > 0) {
            where.append(" AND o.status = ?");
            params.add(status);
        }

        String countSql = "SELECT COUNT(*) FROM product_order o" + where;
        String dataSql = "SELECT o.* FROM product_order o" + where
                + " ORDER BY o.create_time DESC LIMIT ?, ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();

            // 总数
            ps = conn.prepareStatement(countSql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            rs = ps.executeQuery();
            int total = 0;
            if (rs.next()) total = rs.getInt(1);
            rs.close();
            ps.close();

            // 数据
            ps = conn.prepareStatement(dataSql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.setInt(params.size() + 1, (page - 1) * pageSize);
            ps.setInt(params.size() + 2, pageSize);
            rs = ps.executeQuery();

            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> order = toMap(rs);
                list.add(order);
            }
            rs.close();
            ps.close();

            // 查询每个订单的订单项
            for (Map<String, Object> order : list) {
                long orderId = ((Number) order.get("id")).longValue();
                order.put("items", findOrderItems(conn, orderId));
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("list", list);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("查询订单列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 查询订单项
     */
    private List<Map<String, Object>> findOrderItems(Connection conn, long orderId)
            throws SQLException {
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                list.add(toMap(rs));
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * 根据 ID 查询订单
     */
    public Map<String, Object> findById(long orderId) {
        String sql = "SELECT * FROM product_order WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> order = toMap(rs);
                rs.close();
                ps.close();
                order.put("items", findOrderItems(conn, orderId));
                return order;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询订单失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 模拟支付 status 1→2
     */
    public int pay(long orderId, long userId) {
        String sql = "UPDATE product_order SET status = 2, pay_time = NOW() "
                + "WHERE id = ? AND user_id = ? AND status = 1";
        return executeUpdate(sql, orderId, userId);
    }

    /**
     * 确认完成 status 3→4
     */
    public int complete(long orderId, long userId) {
        String sql = "UPDATE product_order SET status = 4, receive_time = NOW() "
                + "WHERE id = ? AND user_id = ? AND status = 3";
        return executeUpdate(sql, orderId, userId);
    }

    /**
     * 取消订单 status 1→9（仅未支付可取消）
     */
    public int cancel(long orderId, long userId) {
        String sql = "UPDATE product_order SET status = 9 "
                + "WHERE id = ? AND user_id = ? AND status = 1";
        return executeUpdate(sql, orderId, userId);
    }

    /**
     * 查询订单项列表（用于取消时恢复库存）
     */
    public List<Map<String, Object>> findItemsByOrderId(long orderId) {
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                list.add(toMap(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查询订单项失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    private int executeUpdate(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新订单失败", e);
        } finally {
            DBUtil.close(conn, ps);
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
}
