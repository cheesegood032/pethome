package com.pethome.dao;

import com.pethome.util.DBUtil;

import java.sql.*;
import java.util.*;

/**
 * 购物车数据访问对象
 */
public class CartDAO {

    /**
     * 查询用户对某商品+规格是否已有购物车记录
     */
    public Map<String, Object> findByUserProductSpec(long userId, long productId, String spec) {
        String sql = "SELECT * FROM cart WHERE user_id = ? AND product_id = ? AND spec = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.setString(3, spec != null ? spec : "");
            rs = ps.executeQuery();
            if (rs.next()) {
                return toMap(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询购物车失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 新增购物车记录
     */
    public long insert(long userId, long productId, int quantity, String spec) {
        String sql = "INSERT INTO cart(user_id, product_id, quantity, spec, create_time) VALUES(?,?,?,?,NOW())";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);
            ps.setString(4, spec != null ? spec : "");
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getLong(1) : -1;
        } catch (SQLException e) {
            throw new RuntimeException("添加购物车失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 累加已有记录的数量
     */
    public int addQuantity(long cartId, int quantity) {
        String sql = "UPDATE cart SET quantity = quantity + ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setLong(2, cartId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新购物车数量失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 获取用户购物车列表（JOIN product 表）
     */
    public List<Map<String, Object>> findListByUserId(long userId) {
        String sql = "SELECT c.id, c.user_id, c.product_id, c.quantity, c.spec, c.create_time, "
                + "p.name AS product_name, p.image AS product_image, p.price, p.stock, p.status "
                + "FROM cart c LEFT JOIN product p ON c.product_id = p.id "
                + "WHERE c.user_id = ? ORDER BY c.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                list.add(toMap(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查询购物车列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 修改数量
     */
    public int updateQuantity(long cartId, int quantity) {
        String sql = "UPDATE cart SET quantity = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setLong(2, cartId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("修改购物车数量失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 删除单条购物车记录
     */
    public int delete(long cartId, long userId) {
        String sql = "DELETE FROM cart WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, cartId);
            ps.setLong(2, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除购物车记录失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 清空用户购物车
     */
    public int clearByUserId(long userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("清空购物车失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 根据 ID 列表和用户 ID 查询购物车（用于下单）
     */
    public List<Map<String, Object>> findByIdsAndUserId(List<Long> cartIds, long userId) {
        if (cartIds == null || cartIds.isEmpty()) return new ArrayList<>();
        StringBuilder sb = new StringBuilder(
                "SELECT c.*, p.name AS product_name, p.price, p.stock, p.image, p.status "
                        + "FROM cart c LEFT JOIN product p ON c.product_id = p.id "
                        + "WHERE c.user_id = ? AND c.id IN (");
        for (int i = 0; i < cartIds.size(); i++) {
            sb.append(i > 0 ? ",?" : "?");
        }
        sb.append(")");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sb.toString());
            ps.setLong(1, userId);
            for (int i = 0; i < cartIds.size(); i++) {
                ps.setLong(i + 2, cartIds.get(i));
            }
            rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                list.add(toMap(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查询购物车失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 批量删除购物车记录（事务中使用，传入连接）
     */
    public int deleteByIds(Connection conn, List<Long> cartIds, long userId) throws SQLException {
        if (cartIds == null || cartIds.isEmpty()) return 0;
        StringBuilder sb = new StringBuilder("DELETE FROM cart WHERE user_id = ? AND id IN (");
        for (int i = 0; i < cartIds.size(); i++) {
            sb.append(i > 0 ? ",?" : "?");
        }
        sb.append(")");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sb.toString());
            ps.setLong(1, userId);
            for (int i = 0; i < cartIds.size(); i++) {
                ps.setLong(i + 2, cartIds.get(i));
            }
            return ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
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
