package com.pethome.service;

import com.pethome.dao.FosterPackageDao;
import com.pethome.model.FosterPackage;

import java.sql.SQLException;
import java.util.List;

public class FosterPackageService {
    private FosterPackageDao packageDao = new FosterPackageDao();

    public boolean addPackage(FosterPackage pkg) {
        try {
            return packageDao.insert(pkg) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("添加套餐失败: " + e.getMessage());
        }
    }

    public FosterPackage getPackageById(Integer id) {
        try {
            return packageDao.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("获取套餐信息失败");
        }
    }

    public List<FosterPackage> getAllPackages() {
        try {
            return packageDao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("获取套餐列表失败");
        }
    }

    public List<FosterPackage> getPackagesByStatus(Integer status) {
        try {
            return packageDao.findAllWithStatus(status);
        } catch (SQLException e) {
            throw new RuntimeException("获取套餐列表失败");
        }
    }

    public boolean updatePackage(FosterPackage pkg) {
        try {
            return packageDao.update(pkg) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("更新套餐失败");
        }
    }

    public boolean updateStatus(Integer id, Integer status) {
        try {
            return packageDao.updateStatus(id, status) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("更新套餐状态失败");
        }
    }

    public boolean deletePackage(Integer id) {
        try {
            return packageDao.delete(id) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("删除套餐失败");
        }
    }
}
