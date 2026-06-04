package com.pethome.dao;

import com.pethome.model.Admin;
import com.pethome.util.DBUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

public class AdminDao {
    private QueryRunner qr = new QueryRunner();

    public Admin findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM admin WHERE username = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(Admin.class), username);
    }

    public Admin findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(Admin.class), username, password);
    }

    public Admin findByPhoneAndPassword(String phone, String password) throws SQLException {
        String sql = "SELECT * FROM admin WHERE phone = ? AND password = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(Admin.class), phone, password);
    }

    public Admin findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM admin WHERE id = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(Admin.class), id);
    }

    public int insert(Admin admin) throws SQLException {
        String sql = "INSERT INTO admin(username, password, phone, real_name, role) VALUES(?, ?, ?, ?, ?)";
        return qr.update(DBUtil.getConnection(), sql, admin.getUsername(), admin.getPassword(),
                        admin.getPhone(), admin.getRealName(), admin.getRole());
    }

    public int update(Admin admin) throws SQLException {
        String sql = "UPDATE admin SET phone = ?, real_name = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, admin.getPhone(), admin.getRealName(), admin.getId());
    }

    public int updatePassword(Integer id, String newPassword) throws SQLException {
        String sql = "UPDATE admin SET password = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, newPassword, id);
    }

    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM admin WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, id);
    }
}
