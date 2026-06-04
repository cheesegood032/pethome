package com.pethome.dao;

import com.pethome.model.User;
import com.pethome.util.DBUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
    private QueryRunner qr = new QueryRunner();

    public int insert(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO user(username, password, phone, email, address) VALUES(?, ?, ?, ?, ?)";
        return qr.update(conn, sql, user.getUsername(), user.getPassword(), user.getPhone(), user.getEmail(), user.getAddress());
    }

    public User findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(User.class), id);
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(User.class), username);
    }

    public User findByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM user WHERE phone = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(User.class), phone);
    }

    public User findByUsernameOrPhone(String loginName) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ? OR phone = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(User.class), loginName, loginName);
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM user ORDER BY create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(User.class));
    }

    public List<User> findByPage(int offset, int limit) throws SQLException {
        String sql = "SELECT * FROM user ORDER BY create_time DESC LIMIT ?, ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(User.class), offset, limit);
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM user";
        return ((Long)qr.query(DBUtil.getConnection(), sql, new ScalarHandler<>())).intValue();
    }

    public int update(User user) throws SQLException {
        String sql = "UPDATE user SET phone = ?, email = ?, address = ?, avatar = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, user.getPhone(), user.getEmail(), user.getAddress(), user.getAvatar(), user.getId());
    }

    public int updatePassword(Integer userId, String newPassword) throws SQLException {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, newPassword, userId);
    }

    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, id);
    }

    public List<User> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM user WHERE username LIKE ? OR phone LIKE ? ORDER BY create_time DESC";
        String pattern = "%" + keyword + "%";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(User.class), pattern, pattern);
    }
}
