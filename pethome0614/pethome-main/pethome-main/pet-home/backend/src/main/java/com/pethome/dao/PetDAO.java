package com.pethome.dao;

import com.pethome.util.DBUtil;

import java.sql.*;
import java.util.*;

/**
 * 宠物数据访问对象
 */
public class PetDAO {

    /**
     * 查询用户名下所有宠物
     */
    public List<Map<String, Object>> findByUserId(long userId) {
        String sql = "SELECT * FROM pet WHERE user_id = ? ORDER BY create_time DESC";
        return queryList(sql, userId);
    }

    /**
     * 根据 ID 查询宠物
     */
    public Map<String, Object> findById(long id) {
        String sql = "SELECT * FROM pet WHERE id = ?";
        return queryOne(sql, id);
    }

    /**
     * 新增宠物
     */
    public long insert(long userId, String name, String species, String breed,
                       String age, String gender, String weight, String image,
                       String healthStatus, String remark) {
        String sql = "INSERT INTO pet(user_id, name, species, breed, age, gender, weight, "
                + "image, health_status, remark, create_time, update_time) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,NOW(),NOW())";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setString(2, name);
            ps.setString(3, species);
            ps.setString(4, breed);
            ps.setString(5, age);
            ps.setString(6, gender);
            ps.setString(7, weight);
            ps.setString(8, image);
            ps.setString(9, healthStatus);
            ps.setString(10, remark);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getLong(1) : -1;
        } catch (SQLException e) {
            throw new RuntimeException("新增宠物失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 修改宠物信息
     */
    public int update(long id, long userId, Map<String, Object> fields) {
        if (fields == null || fields.isEmpty()) return 0;
        StringBuilder sb = new StringBuilder("UPDATE pet SET ");
        List<Object> params = new ArrayList<>();
        String[] allowed = {"name", "species", "breed", "age", "gender", "weight",
                "image", "health_status", "remark"};
        boolean first = true;
        for (String key : allowed) {
            if (fields.containsKey(key)) {
                if (!first) sb.append(", ");
                sb.append(key).append(" = ?");
                params.add(fields.get(key));
                first = false;
            }
        }
        if (params.isEmpty()) return 0;
        sb.append(", update_time = NOW() WHERE id = ? AND user_id = ?");
        params.add(id);
        params.add(userId);

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sb.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("修改宠物失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 删除宠物
     */
    public int delete(long id, long userId) {
        String sql = "DELETE FROM pet WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ps.setLong(2, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除宠物失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 根据 ID 列表查询宠物（寄养订单用）
     */
    public List<Map<String, Object>> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT * FROM pet WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sb.append(i > 0 ? ",?" : "?");
        }
        sb.append(")");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sb.toString());
            for (int i = 0; i < ids.size(); i++) {
                ps.setLong(i + 1, ids.get(i));
            }
            rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) list.add(toMap(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查询宠物失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
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
            throw new RuntimeException("查询宠物列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
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
            if (rs.next()) return toMap(rs);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询宠物失败", e);
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
