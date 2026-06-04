package com.pethome.dao;

import com.pethome.model.OrderItem;
import com.pethome.util.DBUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderItemDao {
    private QueryRunner qr = new QueryRunner();

    public int insert(Connection conn, OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_item(order_id, product_id, product_name, price, quantity, spec) VALUES(?, ?, ?, ?, ?, ?)";
        return qr.update(conn, sql, item.getOrderId(), item.getProductId(), item.getProductName(),
                item.getPrice(), item.getQuantity(), item.getSpec());
    }

    public List<OrderItem> findByOrderId(Integer orderId) throws SQLException {
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(OrderItem.class), orderId);
    }

    public int deleteByOrderId(Integer orderId) throws SQLException {
        String sql = "DELETE FROM order_item WHERE order_id = ?";
        return qr.update(DBUtil.getConnection(), sql, orderId);
    }
}
