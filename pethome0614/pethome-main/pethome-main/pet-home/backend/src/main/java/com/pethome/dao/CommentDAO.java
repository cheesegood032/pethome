package com.pethome.dao;

import com.pethome.util.DBUtil;

import java.sql.*;
import java.util.*;

/**
 * 评论数据访问对象
 */
public class CommentDAO {

    /**
     * 新增评论
     */
    public long insert(long orderItemId, long productId, long userId,
                       int rating, String content) {
        String sql = "INSERT INTO comment(order_item_id, product_id, user_id, rating, content, create_time) "
                + "VALUES(?,?,?,?,?,NOW())";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, orderItemId);
            ps.setLong(2, productId);
            ps.setLong(3, userId);
            ps.setInt(4, rating);
            ps.setString(5, content);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getLong(1) : -1;
        } catch (SQLException e) {
            throw new RuntimeException("新增评论失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 检查某个订单项是否已评论
     */
    public boolean hasCommented(long orderItemId) {
        String sql = "SELECT COUNT(*) FROM comment WHERE order_item_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderItemId);
            rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("查询评论状态失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 按商品ID查询评论列表（含用户昵称），按时间倒序
     */
    public List<Map<String, Object>> findByProductId(long productId) {
        String sql = "SELECT c.*, u.username "
                + "FROM comment c "
                + "LEFT JOIN user u ON c.user_id = u.id "
                + "WHERE c.product_id = ? "
                + "ORDER BY c.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, productId);
            rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                list.add(toMap(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查询商品评论失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 管理员：分页查询所有评论（含用户名和商品名）
     */
    public Map<String, Object> findAll(int page, int pageSize, String keyword) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            where.append(" AND (u.username LIKE ? OR c.content LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
        }

        String countSql = "SELECT COUNT(*) FROM comment c "
                + "LEFT JOIN user u ON c.user_id = u.id" + where;
        String dataSql = "SELECT c.*, u.username, "
                + "(SELECT p.name FROM product p WHERE p.id = c.product_id) AS product_name "
                + "FROM comment c "
                + "LEFT JOIN user u ON c.user_id = u.id"
                + where
                + " ORDER BY c.create_time DESC LIMIT ?, ?";

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
                list.add(toMap(rs));
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("list", list);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("查询评论列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 管理员回复评论
     */
    public int reply(long commentId, String replyContent) {
        String sql = "UPDATE comment SET reply = ?, reply_time = NOW() WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, replyContent);
            ps.setLong(2, commentId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("回复评论失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 查询某订单下已评论的订单项ID列表
     */
    public List<Long> findCommentedItemIds(long orderId) {
        String sql = "SELECT c.order_item_id FROM comment c "
                + "INNER JOIN order_item oi ON c.order_item_id = oi.id "
                + "WHERE oi.order_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            rs = ps.executeQuery();
            List<Long> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getLong(1));
            }
            return ids;
        } catch (SQLException e) {
            throw new RuntimeException("查询已评论订单项失败", e);
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
}
