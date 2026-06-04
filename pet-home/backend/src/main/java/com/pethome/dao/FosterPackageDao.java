package com.pethome.dao;

import com.pethome.model.FosterPackage;
import com.pethome.util.DBUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class FosterPackageDao {
    private QueryRunner qr = new QueryRunner();

    public int insert(FosterPackage pkg) throws SQLException {
        String sql = "INSERT INTO foster_package(name, description, price_per_day, services, status) VALUES(?, ?, ?, ?, ?)";
        return qr.update(DBUtil.getConnection(), sql, pkg.getName(), pkg.getDescription(),
                pkg.getPricePerDay(), pkg.getServices(), pkg.getStatus());
    }

    public FosterPackage findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM foster_package WHERE id = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(FosterPackage.class), id);
    }

    public List<FosterPackage> findAll() throws SQLException {
        String sql = "SELECT * FROM foster_package WHERE status = 1 ORDER BY price_per_day ASC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(FosterPackage.class));
    }

    public List<FosterPackage> findAllWithStatus(Integer status) throws SQLException {
        String sql = "SELECT * FROM foster_package WHERE status = ? ORDER BY price_per_day ASC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(FosterPackage.class), status);
    }

    public int update(FosterPackage pkg) throws SQLException {
        String sql = "UPDATE foster_package SET name = ?, description = ?, price_per_day = ?, services = ?, status = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, pkg.getName(), pkg.getDescription(),
                pkg.getPricePerDay(), pkg.getServices(), pkg.getStatus(), pkg.getId());
    }

    public int updateStatus(Integer id, Integer status) throws SQLException {
        String sql = "UPDATE foster_package SET status = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, status, id);
    }

    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM foster_package WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, id);
    }
}
