package com.pethome.dao;

import com.pethome.model.Cart;
import com.pethome.model.Product;
import com.pethome.util.DBUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CartDao {
    private QueryRunner qr = new QueryRunner();

    public int insert(Connection conn, Cart cart) throws SQLException {
        String sql = "INSERT INTO cart(user_id, product_id, quantity, spec) VALUES(?, ?, ?, ?)";
        return qr.update(conn, sql, cart.getUserId(), cart.getProductId(), cart.getQuantity(), cart.getSpec());
    }

    public List<Cart> findByUserId(Integer userId) throws SQLException {
        String sql = "SELECT c.*, p.name as product_name, p.price, p.image, p.stock " +
                     "FROM cart c JOIN product p ON c.product_id = p.id WHERE c.user_id = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(Cart.class), userId);
    }

    public Cart findByUserIdAndProductId(Integer userId, Integer productId) throws SQLException {
        String sql = "SELECT * FROM cart WHERE user_id = ? AND product_id = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(Cart.class), userId, productId);
    }

    public Cart findById(Integer id) throws SQLException {
        String sql = "SELECT c.*, p.name as product_name, p.price, p.image, p.stock " +
                     "FROM cart c JOIN product p ON c.product_id = p.id WHERE c.id = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(Cart.class), id);
    }

    public int updateQuantity(Integer id, Integer quantity) throws SQLException {
        String sql = "UPDATE cart SET quantity = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, quantity, id);
    }

    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM cart WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, id);
    }

    public int deleteByUserId(Integer userId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        return qr.update(DBUtil.getConnection(), sql, userId);
    }

    public int deleteByIds(List<Integer> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) return 0;
        StringBuilder sql = new StringBuilder("DELETE FROM cart WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sql.append(i > 0 ? ",?" : "?");
        }
        sql.append(")");
        return qr.update(DBUtil.getConnection(), sql.toString(), ids.toArray());
    }
}
