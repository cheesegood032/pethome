package com.pethome.dao;

import com.pethome.model.Product;
import com.pethome.util.DBUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductDao {
    private QueryRunner qr = new QueryRunner();

    public int insert(Product product) throws SQLException {
        String sql = "INSERT INTO product(name, category, pet_type, price, stock, image, description, spec, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return qr.update(DBUtil.getConnection(), sql, product.getName(), product.getCategory(),
                product.getPetType(), product.getPrice(), product.getStock(), product.getImage(),
                product.getDescription(), product.getSpec(), product.getStatus());
    }

    public Product findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM product WHERE id = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(Product.class), id);
    }

    public List<Product> findAll() throws SQLException {
        String sql = "SELECT * FROM product WHERE status = 1 ORDER BY create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(Product.class));
    }

    public List<Product> findByPage(int offset, int limit) throws SQLException {
        String sql = "SELECT * FROM product WHERE status = 1 ORDER BY create_time DESC LIMIT ?, ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(Product.class), offset, limit);
    }

    public List<Product> findAllWithStatus(Integer status) throws SQLException {
        String sql = "SELECT * FROM product WHERE status = ? ORDER BY create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(Product.class), status);
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM product WHERE status = 1";
        return ((Long)qr.query(DBUtil.getConnection(), sql, new ScalarHandler<>())).intValue();
    }

    public int countWithStatus(Integer status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM product WHERE status = ?";
        return ((Long)qr.query(DBUtil.getConnection(), sql, new ScalarHandler<>(), status)).intValue();
    }

    public List<Product> search(String keyword, String category, String petType) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE status = 1");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (name LIKE ? OR description LIKE ?)");
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND category = ?");
        }
        if (petType != null && !petType.isEmpty()) {
            sql.append(" AND pet_type = ?");
        }
        sql.append(" ORDER BY create_time DESC");

        return qr.query(DBUtil.getConnection(), sql.toString(), new BeanListHandler<>(Product.class),
                buildSearchParams(keyword, category, petType));
    }

    private Object[] buildSearchParams(String keyword, String category, String petType) {
        int count = 0;
        if (keyword != null && !keyword.isEmpty()) count += 2;
        if (category != null && !category.isEmpty()) count++;
        if (petType != null && !petType.isEmpty()) count++;

        Object[] params = new Object[count];
        int idx = 0;
        if (keyword != null && !keyword.isEmpty()) {
            String pattern = "%" + keyword + "%";
            params[idx++] = pattern;
            params[idx++] = pattern;
        }
        if (category != null && !category.isEmpty()) {
            params[idx++] = category;
        }
        if (petType != null && !petType.isEmpty()) {
            params[idx] = petType;
        }
        return params;
    }

    public int update(Product product) throws SQLException {
        String sql = "UPDATE product SET name = ?, category = ?, pet_type = ?, price = ?, stock = ?, " +
                     "image = ?, description = ?, spec = ?, status = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, product.getName(), product.getCategory(),
                product.getPetType(), product.getPrice(), product.getStock(), product.getImage(),
                product.getDescription(), product.getSpec(), product.getStatus(), product.getId());
    }

    public int updateStock(Integer id, int stock) throws SQLException {
        String sql = "UPDATE product SET stock = stock - ? WHERE id = ? AND stock >= ?";
        return qr.update(DBUtil.getConnection(), sql, stock, id, stock);
    }

    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM product WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, id);
    }

    public int updateStatus(Integer id, Integer status) throws SQLException {
        String sql = "UPDATE product SET status = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, status, id);
    }
}
