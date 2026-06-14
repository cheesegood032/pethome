package com.pethome.dao;

import com.pethome.util.DBUtil;

import java.sql.*;
import java.util.*;

/**
 * 用户数据访问对象
 */
public class UserDAO {

    /**
     * 根据用户名查询用户
     */
    public Map<String, Object> findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                return toMap(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询用户失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 根据手机号查询用户
     */
    public Map<String, Object> findByPhone(String phone) {
        String sql = "SELECT * FROM user WHERE phone = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            rs = ps.executeQuery();
            if (rs.next()) {
                return toMap(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询用户失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 根据 ID 查询用户
     */
    public Map<String, Object> findById(long id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return toMap(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询用户失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 注册新用户
     */
    public long insert(String username, String password, String phone, String address) {
        String sql = "INSERT INTO user(username, password, phone, address, create_time, update_time) VALUES(?,?,?,?,NOW(),NOW())";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("注册用户失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 修改密码
     */
    public int updatePassword(long userId, String newPassword) {
        String sql = "UPDATE user SET password = ?, update_time = NOW() WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setLong(2, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("修改密码失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 修改手机号
     */
    public int updatePhone(long userId, String phone) {
        String sql = "UPDATE user SET phone = ?, update_time = NOW() WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ps.setLong(2, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("修改手机号失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 修改地址
     */
    public int updateAddress(long userId, String address) {
        String sql = "UPDATE user SET address = ?, update_time = NOW() WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, address);
            ps.setLong(2, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("修改地址失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 修改个人信息（email, avatar 等）
     */
    public int updateInfo(long userId, Map<String, Object> fields) {
        if (fields == null || fields.isEmpty()) return 0;

        StringBuilder sb = new StringBuilder("UPDATE user SET ");
        List<Object> params = new ArrayList<>();
        // 允许更新的字段白名单
        String[] allowed = {"phone", "email", "avatar", "address"};
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

        sb.append(", update_time = NOW() WHERE id = ?");
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
            e.printStackTrace();
            throw new RuntimeException("修改用户信息失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * ResultSet 转 Map
     */
    private Map<String, Object> toMap(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            String colName = meta.getColumnLabel(i);
            map.put(colName, rs.getObject(i));
        }
        return map;
    }
}
