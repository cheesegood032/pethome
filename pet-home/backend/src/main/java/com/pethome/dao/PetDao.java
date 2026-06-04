package com.pethome.dao;

import com.pethome.model.Pet;
import com.pethome.util.DBUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

public class PetDao {
    private QueryRunner qr = new QueryRunner();

    public int insert(Pet pet) throws SQLException {
        String sql = "INSERT INTO pet(user_id, name, species, breed, age, gender, weight, image, health_status, remark) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return qr.update(DBUtil.getConnection(), sql, pet.getUserId(), pet.getName(), pet.getSpecies(),
                pet.getBreed(), pet.getAge(), pet.getGender(), pet.getWeight(), pet.getImage(),
                pet.getHealthStatus(), pet.getRemark());
    }

    public Pet findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM pet WHERE id = ?";
        return qr.query(DBUtil.getConnection(), sql, new BeanHandler<>(Pet.class), id);
    }

    public List<Pet> findByUserId(Integer userId) throws SQLException {
        String sql = "SELECT * FROM pet WHERE user_id = ? ORDER BY create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(Pet.class), userId);
    }

    public List<Pet> findAll() throws SQLException {
        String sql = "SELECT p.*, u.username FROM pet p LEFT JOIN user u ON p.user_id = u.id ORDER BY p.create_time DESC";
        return qr.query(DBUtil.getConnection(), sql, new BeanListHandler<>(Pet.class));
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pet";
        return ((Long)qr.query(DBUtil.getConnection(), sql, new ScalarHandler<>())).intValue();
    }

    public int update(Pet pet) throws SQLException {
        String sql = "UPDATE pet SET name = ?, species = ?, breed = ?, age = ?, gender = ?, " +
                     "weight = ?, image = ?, health_status = ?, remark = ? WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, pet.getName(), pet.getSpecies(), pet.getBreed(),
                pet.getAge(), pet.getGender(), pet.getWeight(), pet.getImage(), pet.getHealthStatus(),
                pet.getRemark(), pet.getId());
    }

    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM pet WHERE id = ?";
        return qr.update(DBUtil.getConnection(), sql, id);
    }
}
