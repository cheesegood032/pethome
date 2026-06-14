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
}
