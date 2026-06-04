package com.pethome.dao;

import com.pethome.model.ProductOrder;
import com.pethome.model.OrderItem;
import com.pethome.util.DBUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductOrderDao {
    private QueryRunner qr = new QueryRunner();

    public int insert(Connection conn, ProductOrder order) throws SQLException {
        String sql = "INSERT INTO product_order(order_no, user_id, total_price, status, receiver_name, " +
                     "receiver_phone, receiver_address, remark) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        return qr.update(conn, sql, order.getOrderNo(), order.getUserId(), order.getTotalPrice(),
                order.getStatus(), order.getReceiverName(), order.getReceiverPhone(),
                order.getReceiverAddress(), order.getRemark());
    }

    public ProductOrder findById(Integer id) throws SQLException {
        String sql = "SELECT o.*, u.username, u.phone as user_phone FROM product_order o " +
                     "LEFT JOIN user u ON o.user_id = u.id WHERE o.id = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(ProductOrder.class), id);
    }

    public ProductOrder findByOrderNo(String orderNo) throws SQLException {
        String sql = "SELECT * FROM product_order WHERE order_no = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(ProductOrder.class), orderNo);
    }

    public List<ProductOrder> findByUserId(Integer userId) throws SQLException {
        String sql = "SELECT * FROM product_order WHERE user_id = ? ORDER BY create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(ProductOrder.class), userId);
    }

    public List<ProductOrder> findByUserIdAndStatus(Integer userId, Integer status) throws SQLException {
        String sql = "SELECT * FROM product_order WHERE user_id = ? AND status = ? ORDER BY create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(ProductOrder.class), userId, status);
    }

    public List<ProductOrder> findAll() throws SQLException {
        String sql = "SELECT o.*, u.username, u.phone as user_phone FROM product_order o " +
                     "LEFT JOIN user u ON o.user_id = u.id ORDER BY o.create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(ProductOrder.class));
    }

    public List<ProductOrder> findByStatus(Integer status) throws SQLException {
        String sql = "SELECT o.*, u.username, u.phone as user_phone FROM product_order o " +
                     "LEFT JOIN user u ON o.user_id = u.id WHERE o.status = ? ORDER BY o.create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(ProductOrder.class), status);
    }

    public List<ProductOrder> findByPage(int offset, int limit) throws SQLException {
        String sql = "SELECT o.*, u.username, u.phone as user_phone FROM product_order o " +
                     "LEFT JOIN user u ON o.user_id = u.id ORDER BY o.create_time DESC LIMIT ?, ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(ProductOrder.class), offset, limit);
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM product_order";
        return ((Long)qr.query(DBUtil.getConnection(), sql, new ScalarHandler<>())).intValue();
    }

    public int updateStatus(Integer id, Integer status) throws SQLException {
        String sql = "UPDATE product_order SET status = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, status, id);
    }

    public int updatePayTime(Integer id) throws SQLException {
        String sql = "UPDATE product_order SET status = 2, pay_time = NOW() WHERE id = ? AND status = 1";
        return qr.update(DBUtil.getConnection(), sql, id);
    }

    public int updateShipTime(Integer id) throws SQLException {
        String sql = "UPDATE product_order SET status = 3, ship_time = NOW() WHERE id = ? AND status = 2";
        return qr.update(DBUtil.getConnection(), sql, id);
    }

    public int updateReceiveTime(Integer id) throws SQLException {
        String sql = "UPDATE product_order SET status = 4, receive_time = NOW() WHERE id = ? AND status = 3";
        return qr.update(DBUtil.getConnection(), sql, id);
    }

    public List<ProductOrder> search(String keyword, Integer status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT o.*, u.username, u.phone as user_phone FROM product_order o " +
                     "LEFT JOIN user u ON o.user_id = u.id WHERE 1=1");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (o.order_no LIKE ? OR u.username LIKE ? OR u.phone LIKE ? OR o.receiver_name LIKE ?)");
        }
        if (status != null) {
            sql.append(" AND o.status = ?");
        }
        sql.append(" ORDER BY o.create_time DESC");

        return qr.query(DBUtil.getConnection(), sql.toString(), new BeanListHandler<>(ProductOrder.class),
                buildSearchParams(keyword, status));
    }

    private Object[] buildSearchParams(String keyword, Integer status) {
        int count = 0;
        if (keyword != null && !keyword.isEmpty()) count += 4;
        if (status != null) count++;

        Object[] params = new Object[count];
        int idx = 0;
        if (keyword != null && !keyword.isEmpty()) {
            String pattern = "%" + keyword + "%";
            params[idx++] = pattern;
            params[idx++] = pattern;
            params[idx++] = pattern;
            params[idx++] = pattern;
        }
        if (status != null) {
            params[idx] = status;
        }
        return params;
    }
}
