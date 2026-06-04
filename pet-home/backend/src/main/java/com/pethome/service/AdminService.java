package com.pethome.service;

import com.pethome.config.AppConfig;
import com.pethome.dao.AdminDao;
import com.pethome.mock.MockData;
import com.pethome.model.Admin;
import com.pethome.util.MD5Util;

public class AdminService {
    private AdminDao adminDao = new AdminDao();

    public Admin login(String loginName, String password) {
        if (AppConfig.USE_MOCK_DATA) {
            Admin admin = MockData.loginAdmin(loginName, password);
            if (admin == null) {
                throw new RuntimeException("管理员账号或密码错误");
            }
            admin.setPassword(null);
            return admin;
        }
        
        try {
            Admin admin = null;
            if (loginName.matches("^1\\d{10}$")) {
                admin = adminDao.findByPhoneAndPassword(loginName, MD5Util.md5(password));
            } else {
                admin = adminDao.findByUsernameAndPassword(loginName, MD5Util.md5(password));
            }
            if (admin == null) {
                throw new RuntimeException("管理员账号或密码错误");
            }
            admin.setPassword(null);
            return admin;
        } catch (Exception e) {
            throw new RuntimeException("登录失败: " + e.getMessage());
        }
    }

    public Admin getAdminById(Integer id) {
        if (AppConfig.USE_MOCK_DATA) {
            // MockData 中管理员信息是固定的
            Admin admin = new Admin();
            admin.setId(1);
            admin.setUsername("admin");
            admin.setName("管理员");
            admin.setPhone("13800138000");
            return id == 1 ? admin : null;
        }
        
        try {
            Admin admin = adminDao.findById(id);
            if (admin != null) {
                admin.setPassword(null);
            }
            return admin;
        } catch (Exception e) {
            throw new RuntimeException("获取管理员信息失败");
        }
    }

    public boolean updateAdmin(Admin admin) {
        if (AppConfig.USE_MOCK_DATA) {
            // Mock 模式不支持更新管理员
            return true;
        }
        
        try {
            return adminDao.update(admin) > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新管理员信息失败");
        }
    }

    public boolean changePassword(Integer adminId, String oldPassword, String newPassword) {
        if (AppConfig.USE_MOCK_DATA) {
            // Mock 模式简单验证
            Admin admin = MockData.loginAdmin("admin", oldPassword);
            if (admin == null) {
                throw new RuntimeException("原密码错误");
            }
            return true;
        }
        
        try {
            Admin admin = adminDao.findById(adminId);
            if (admin == null) {
                throw new RuntimeException("管理员不存在");
            }
            if (!MD5Util.verify(oldPassword, admin.getPassword())) {
                throw new RuntimeException("原密码错误");
            }
            return adminDao.updatePassword(adminId, MD5Util.md5(newPassword)) > 0;
        } catch (Exception e) {
            throw new RuntimeException("修改密码失败");
        }
    }
}