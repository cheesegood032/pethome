package com.pethome.dao;

import com.pethome.util.DBUtil;

import java.sql.*;
import java.util.*;

/**
 * 门店数据访问对象
 */
public class StoreDAO {

    /**
     * 查询所有启用的门店
     */
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM store WHERE status = 1 ORDER BY create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) list.add(toMap(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查询门店列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 根据 ID 查询门店
     */
    public Map<String, Object> findById(long id) {
        String sql = "SELECT * FROM store WHERE id = ?";
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
            throw new RuntimeException("查询门店失败", e);
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
