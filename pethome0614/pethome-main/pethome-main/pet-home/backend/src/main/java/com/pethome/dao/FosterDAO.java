package com.pethome.dao;

import com.pethome.util.DBUtil;

import java.sql.*;
import java.util.*;

/**
 * 寄养数据访问对象
 */
public class FosterDAO {

    /* =========== 套餐相关 =========== */

    /**
     * 查询所有启用的寄养套餐
     */
    public List<Map<String, Object>> findAllPackages() {
        String sql = "SELECT * FROM foster_package WHERE status = 1 ORDER BY price_per_day ASC";
        return queryList(sql);
    }

    /**
     * 根据 ID 查询套餐
     */
    public Map<String, Object> findPackageById(long id) {
        String sql = "SELECT * FROM foster_package WHERE id = ?";
        return queryOne(sql, id);
    }

    /* =========== 寄养订单相关 =========== */

    /**
     * 生成寄养订单号
     */
    public String generateOrderNo() {
        long ts = System.currentTimeMillis();
        int rand = (int) (Math.random() * 9000) + 1000;
        return "FO" + ts + rand;
    }

    /**
     * 创建寄养订单（事务中使用）
     */
    public long insertFosterOrder(Connection conn, String orderNo, long userId, long packageId,
                                   String startDate, String endDate, int totalDays,
                                   double totalPrice, String remark) throws SQLException {
        String sql = "INSERT INTO foster_order(order_no, user_id, package_id, start_date, end_date, "
                + "total_days, total_price, status, remark, create_time) "
                + "VALUES(?,?,?,?,?,?,?,1,?,NOW())";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, orderNo);
            ps.setLong(2, userId);
            ps.setLong(3, packageId);
            ps.setString(4, startDate);
            ps.setString(5, endDate);
            ps.setInt(6, totalDays);
            ps.setDouble(7, totalPrice);
            ps.setString(8, remark);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getLong(1) : -1;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * 插入寄养订单与宠物关联
     */
    public void insertFosterOrderPet(Connection conn, long fosterOrderId, long petId) throws SQLException {
        String sql = "INSERT INTO foster_order_pet(foster_order_id, pet_id) VALUES(?,?)";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setLong(1, fosterOrderId);
            ps.setLong(2, petId);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * 用户寄养订单列表
     */
    public Map<String, Object> findOrdersByUserId(long userId, Integer status,
                                                    int page, int pageSize) {
        StringBuilder where = new StringBuilder(" WHERE fo.user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && status > 0) {
            where.append(" AND fo.status = ?");
            params.add(status);
        }

        String countSql = "SELECT COUNT(*) FROM foster_order fo" + where;
        String dataSql = "SELECT fo.*, fp.name AS package_name, fp.price_per_day, fp.services "
                + "FROM foster_order fo LEFT JOIN foster_package fp ON fo.package_id = fp.id"
                + where + " ORDER BY fo.create_time DESC LIMIT ?, ?";

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
            rs.close();
            ps.close();

            // 查询每个订单关联的宠物
            for (Map<String, Object> order : list) {
                long orderId = ((Number) order.get("id")).longValue();
                order.put("pets", findPetsByFosterOrderId(conn, orderId));
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("list", list);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("查询寄养订单列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 查询寄养订单关联的宠物列表
     */
    public List<Map<String, Object>> findPetsByFosterOrderId(Connection conn, long fosterOrderId)
            throws SQLException {
        String sql = "SELECT p.* FROM foster_order_pet fop "
                + "LEFT JOIN pet p ON fop.pet_id = p.id "
                + "WHERE fop.foster_order_id = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setLong(1, fosterOrderId);
            rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) list.add(toMap(rs));
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * 根据 ID 查询寄养订单
     */
    public Map<String, Object> findOrderById(long id) {
        String sql = "SELECT fo.*, fp.name AS package_name, fp.price_per_day, fp.services "
                + "FROM foster_order fo LEFT JOIN foster_package fp ON fo.package_id = fp.id "
                + "WHERE fo.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> order = toMap(rs);
                rs.close();
                ps.close();
                order.put("pets", findPetsByFosterOrderId(conn, id));
                return order;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询寄养订单失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 模拟支付 status 2→3
     */
    public int payOrder(long orderId, long userId) {
        String sql = "UPDATE foster_order SET status = 3 WHERE id = ? AND user_id = ? AND status = 2";
        return executeUpdate(sql, orderId, userId);
    }

    /**
     * 取消预约 status 1→9
     */
    public int cancelOrder(long orderId, long userId) {
        String sql = "UPDATE foster_order SET status = 9 WHERE id = ? AND user_id = ? AND status = 1";
        return executeUpdate(sql, orderId, userId);
    }

    /* =========== 管理端方法 =========== */

    /**
     * 管理端寄养订单列表
     */
    public Map<String, Object> findOrdersAdmin(String username, Integer status,
                                                 String startDateBegin, String startDateEnd,
                                                 int page, int pageSize) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (username != null && !username.trim().isEmpty()) {
            where.append(" AND u.username LIKE ?");
            params.add("%" + username.trim() + "%");
        }
        if (status != null && status > 0) {
            where.append(" AND fo.status = ?");
            params.add(status);
        }
        if (startDateBegin != null && !startDateBegin.trim().isEmpty()) {
            where.append(" AND fo.start_date >= ?");
            params.add(startDateBegin.trim());
        }
        if (startDateEnd != null && !startDateEnd.trim().isEmpty()) {
            where.append(" AND fo.start_date <= ?");
            params.add(startDateEnd.trim());
        }

        String countSql = "SELECT COUNT(*) FROM foster_order fo "
                + "LEFT JOIN user u ON fo.user_id = u.id" + where;
        String dataSql = "SELECT fo.*, u.username, fp.name AS package_name, "
                + "fp.price_per_day, fp.services "
                + "FROM foster_order fo "
                + "LEFT JOIN user u ON fo.user_id = u.id "
                + "LEFT JOIN foster_package fp ON fo.package_id = fp.id"
                + where + " ORDER BY fo.create_time DESC LIMIT ?, ?";

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
            while (rs.next()) list.add(toMap(rs));
            rs.close();
            ps.close();

            // 每条加载宠物
            for (Map<String, Object> order : list) {
                long orderId = ((Number) order.get("id")).longValue();
                order.put("pets", findPetsByFosterOrderId(conn, orderId));
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("list", list);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("查询管理端寄养订单失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 审核通过 status 1→2
     */
    public int approve(long orderId) {
        String sql = "UPDATE foster_order SET status = 2, audit_time = NOW() WHERE id = ? AND status = 1";
        return executeUpdate(sql, orderId);
    }

    /**
     * 驳回 status 1→8
     */
    public int reject(long orderId, String rejectReason) {
        String sql = "UPDATE foster_order SET status = 8, reject_reason = ?, audit_time = NOW() "
                + "WHERE id = ? AND status = 1";
        return executeUpdate(sql, rejectReason, orderId);
    }

    /**
     * 办理入住 status 3→4
     */
    public int checkin(long orderId) {
        String sql = "UPDATE foster_order SET status = 4, checkin_time = NOW() WHERE id = ? AND status = 3";
        return executeUpdate(sql, orderId);
    }

    /**
     * 标记完成 status 4→5
     */
    public int completeFoster(long orderId) {
        String sql = "UPDATE foster_order SET status = 5, complete_time = NOW() WHERE id = ? AND status = 4";
        return executeUpdate(sql, orderId);
    }

    /**
     * 按月统计订单量
     */
    public List<Map<String, Object>> monthlyStats(int year) {
        String sql = "SELECT MONTH(create_time) AS month, COUNT(*) AS count "
                + "FROM foster_order WHERE YEAR(create_time) = ? "
                + "GROUP BY MONTH(create_time) ORDER BY month";
        return queryList(sql, year);
    }

    /* =========== 辅助方法 =========== */

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
            throw new RuntimeException("更新寄养订单失败", e);
        } finally {
            DBUtil.close(conn, ps);
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
