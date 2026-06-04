package com.pethome.dao;

import com.pethome.model.FosterOrder;
import com.pethome.util.DBUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FosterOrderDao {
    private QueryRunner qr = new QueryRunner();

    public int insert(FosterOrder order) throws SQLException {
        String sql = "INSERT INTO foster_order(order_no, user_id, pet_id, package_id, start_date, end_date, " +
                     "total_days, total_price, status, remark) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return qr.update(DBUtil.getConnection(), sql, order.getOrderNo(), order.getUserId(), order.getPetId(),
                order.getPackageId(), order.getStartDate(), order.getEndDate(), order.getTotalDays(),
                order.getTotalPrice(), order.getStatus(), order.getRemark());
    }

    public FosterOrder findById(Integer id) throws SQLException {
        String sql = "SELECT f.*, u.username, u.phone as user_phone, p.name as pet_name, p.species, " +
                     "pkg.name as package_name, pkg.price_per_day FROM foster_order f " +
                     "LEFT JOIN user u ON f.user_id = u.id " +
                     "LEFT JOIN pet p ON f.pet_id = p.id " +
                     "LEFT JOIN foster_package pkg ON f.package_id = pkg.id " +
                     "WHERE f.id = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(FosterOrder.class), id);
    }

    public FosterOrder findByOrderNo(String orderNo) throws SQLException {
        String sql = "SELECT f.*, u.username, u.phone as user_phone, p.name as pet_name, p.species, " +
                     "pkg.name as package_name, pkg.price_per_day FROM foster_order f " +
                     "LEFT JOIN user u ON f.user_id = u.id " +
                     "LEFT JOIN pet p ON f.pet_id = p.id " +
                     "LEFT JOIN foster_package pkg ON f.package_id = pkg.id " +
                     "WHERE f.order_no = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(FosterOrder.class), orderNo);
    }

    public List<FosterOrder> findByUserId(Integer userId) throws SQLException {
        String sql = "SELECT f.*, p.name as pet_name, pkg.name as package_name FROM foster_order f " +
                     "LEFT JOIN pet p ON f.pet_id = p.id " +
                     "LEFT JOIN foster_package pkg ON f.package_id = pkg.id " +
                     "WHERE f.user_id = ? ORDER BY f.create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(FosterOrder.class), userId);
    }

    public List<FosterOrder> findByUserIdAndStatus(Integer userId, Integer status) throws SQLException {
        String sql = "SELECT f.*, p.name as pet_name, pkg.name as package_name FROM foster_order f " +
                     "LEFT JOIN pet p ON f.pet_id = p.id " +
                     "LEFT JOIN foster_package pkg ON f.package_id = pkg.id " +
                     "WHERE f.user_id = ? AND f.status = ? ORDER BY f.create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(FosterOrder.class), userId, status);
    }

    public List<FosterOrder> findAll() throws SQLException {
        String sql = "SELECT f.*, u.username, u.phone as user_phone, p.name as pet_name, p.species, " +
                     "pkg.name as package_name FROM foster_order f " +
                     "LEFT JOIN user u ON f.user_id = u.id " +
                     "LEFT JOIN pet p ON f.pet_id = p.id " +
                     "LEFT JOIN foster_package pkg ON f.package_id = pkg.id " +
                     "ORDER BY f.create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(FosterOrder.class));
    }

    public List<FosterOrder> findByStatus(Integer status) throws SQLException {
        String sql = "SELECT f.*, u.username, u.phone as user_phone, p.name as pet_name, p.species, " +
                     "pkg.name as package_name FROM foster_order f " +
                     "LEFT JOIN user u ON f.user_id = u.id " +
                     "LEFT JOIN pet p ON f.pet_id = p.id " +
                     "LEFT JOIN foster_package pkg ON f.package_id = pkg.id " +
                     "WHERE f.status = ? ORDER BY f.create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(FosterOrder.class), status);
    }

    public List<FosterOrder> findByDateRange(java.sql.Date startDate, java.sql.Date endDate) throws SQLException {
        String sql = "SELECT f.*, u.username, u.phone as user_phone, p.name as pet_name, p.species, " +
                     "pkg.name as package_name FROM foster_order f " +
                     "LEFT JOIN user u ON f.user_id = u.id " +
                     "LEFT JOIN pet p ON f.pet_id = p.id " +
                     "LEFT JOIN foster_package pkg ON f.package_id = pkg.id " +
                     "WHERE f.start_date >= ? AND f.end_date <= ? ORDER BY f.create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(FosterOrder.class), startDate, endDate);
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM foster_order";
        return ((Long)qr.query(DBUtil.getConnection(), sql, new ScalarHandler<>())).intValue();
    }

    public int updateStatus(Integer id, Integer status) throws SQLException {
        String sql = "UPDATE foster_order SET status = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, status, id);
    }

    public int audit(Integer id, Integer status, String rejectReason) throws SQLException {
        String sql = "UPDATE foster_order SET status = ?, reject_reason = ?, audit_time = NOW() WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, status, rejectReason, id);
    }

    public int complete(Integer id) throws SQLException {
        String sql = "UPDATE foster_order SET status = 4, complete_time = NOW() WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, id);
    }

    public List<FosterOrder> search(Integer userId, Integer status, java.sql.Date startDate, java.sql.Date endDate) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT f.*, u.username, u.phone as user_phone, p.name as pet_name, p.species, " +
                     "pkg.name as package_name FROM foster_order f " +
                     "LEFT JOIN user u ON f.user_id = u.id " +
                     "LEFT JOIN pet p ON f.pet_id = p.id " +
                     "LEFT JOIN foster_package pkg ON f.package_id = pkg.id " +
                     "WHERE 1=1");

        if (userId != null) sql.append(" AND f.user_id = ?");
        if (status != null) sql.append(" AND f.status = ?");
        if (startDate != null) sql.append(" AND f.start_date >= ?");
        if (endDate != null) sql.append(" AND f.end_date <= ?");

        sql.append(" ORDER BY f.create_time DESC");

        return qr.query(DBUtil.getConnection(), sql.toString(), new BeanListHandler<>(FosterOrder.class),
                buildSearchParams(userId, status, startDate, endDate));
    }

    private Object[] buildSearchParams(Integer userId, Integer status, java.sql.Date startDate, java.sql.Date endDate) {
        int count = 0;
        if (userId != null) count++;
        if (status != null) count++;
        if (startDate != null) count++;
        if (endDate != null) count++;

        Object[] params = new Object[count];
        int idx = 0;
        if (userId != null) params[idx++] = userId;
        if (status != null) params[idx++] = status;
        if (startDate != null) params[idx++] = startDate;
        if (endDate != null) params[idx] = endDate;
        return params;
    }
}
