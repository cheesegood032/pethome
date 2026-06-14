package com.pethome.dao;

import com.pethome.util.DBUtil;

import java.sql.*;
import java.util.*;

/**
 * 商品数据访问对象
 */
public class ProductDAO {

    /**
     * 分页查询商品列表（支持关键字、宠物类型、分类筛选，仅 status=1 上架）
     */
    public Map<String, Object> findList(String keyword, String petType,
                                        String category, int page, int pageSize) {
        StringBuilder where = new StringBuilder(" WHERE status = 1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            where.append(" AND name LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }
        if (petType != null && !petType.trim().isEmpty()) {
            where.append(" AND pet_type = ?");
            params.add(petType.trim());
        }
        if (category != null && !category.trim().isEmpty()) {
            where.append(" AND category = ?");
            params.add(category.trim());
        }

        String countSql = "SELECT COUNT(*) FROM product" + where;
        String dataSql = "SELECT * FROM product" + where
                + " ORDER BY create_time DESC LIMIT ?, ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();

            ps = conn.prepareStatement(countSql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            rs = ps.executeQuery();
            int total = 0;
            if (rs.next()) total = rs.getInt(1);
            rs.close();
            ps.close();

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
            throw new RuntimeException("查询商品列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /** 根据 ID 查询商品详情 */
    public Map<String, Object> findById(long id) {
        String sql = "SELECT * FROM product WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return toMap(rs);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询商品详情失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /** 热销Top5（按 sales_count 降序，status=1） */
    public List<Map<String, Object>> findHot() {
        return queryList("SELECT * FROM product WHERE status = 1 ORDER BY sales_count DESC LIMIT 5");
    }

    /** 新品上架Top8（按 id 降序，status=1） */
    public List<Map<String, Object>> findNew() {
        return queryList("SELECT * FROM product WHERE status = 1 ORDER BY id DESC LIMIT 8");
    }

    /** 查询所有上架商品 */
    public List<Map<String, Object>> findAll() {
        return queryList("SELECT * FROM product WHERE status = 1 ORDER BY create_time DESC");
    }

    /** 扣减库存，增加销量（事务中调用，传入连接，乐观锁防超卖） */
    public int deductStock(Connection conn, long productId, int quantity) throws SQLException {
        String sql = "UPDATE product SET stock = stock - ?, sales_count = sales_count + ?, "
                + "update_time = NOW() WHERE id = ? AND stock >= ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, quantity);
            ps.setLong(3, productId);
            ps.setInt(4, quantity);
            return ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /** 恢复库存（取消订单时） */
    public int restoreStock(Connection conn, long productId, int quantity) throws SQLException {
        String sql = "UPDATE product SET stock = stock + ?, sales_count = sales_count - ?, "
                + "update_time = NOW() WHERE id = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, quantity);
            ps.setLong(3, productId);
            return ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    private List<Map<String, Object>> queryList(String sql, Object... params) {
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
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) list.add(toMap(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查询失败", e);
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
