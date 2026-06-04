package com.pethome.service;

import com.pethome.config.AppConfig;
import com.pethome.dao.UserDao;
import com.pethome.mock.MockData;
import com.pethome.model.User;
import com.pethome.util.MD5Util;

import java.util.List;

public class UserService {
    private UserDao userDao = new UserDao();

    public User register(String username, String password, String phone, String email) {
        if (AppConfig.USE_MOCK_DATA) {
            // Mock 模式：检查用户名是否已存在
            List<User> existingUsers = MockData.getUsersByPage(1, Integer.MAX_VALUE);
            for (User u : existingUsers) {
                if (u.getUsername().equals(username)) {
                    throw new RuntimeException("用户名已存在");
                }
                if (phone != null && phone.equals(u.getPhone())) {
                    throw new RuntimeException("手机号已被注册");
                }
            }
            // Mock 模式不加密密码，方便测试
            return MockData.register(username, password, phone, email);
        }
        
        try {
            if (userDao.findByUsername(username) != null) {
                throw new RuntimeException("用户名已存在");
            }
            if (phone != null && userDao.findByPhone(phone) != null) {
                throw new RuntimeException("手机号已被注册");
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(MD5Util.md5(password));
            user.setPhone(phone);
            user.setEmail(email);
            userDao.insert(null, user);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("注册失败: " + e.getMessage());
        }
    }

    public User login(String loginName, String password) {
        if (AppConfig.USE_MOCK_DATA) {
            User user = MockData.login(loginName, password);
            if (user == null) {
                throw new RuntimeException("用户名或密码错误");
            }
            user.setPassword(null);
            return user;
        }
        
        try {
            User user = userDao.findByUsernameOrPhone(loginName);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            if (!MD5Util.verify(password, user.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            user.setPassword(null);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("登录失败: " + e.getMessage());
        }
    }

    public User getUserById(Integer id) {
        if (AppConfig.USE_MOCK_DATA) {
            User user = MockData.getUserById(id);
            if (user != null) {
                user.setPassword(null);
            }
            return user;
        }
        
        try {
            User user = userDao.findById(id);
            if (user != null) {
                user.setPassword(null);
            }
            return user;
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息失败");
        }
    }

    public List<User> getAllUsers() {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getUsersByPage(1, Integer.MAX_VALUE);
        }
        
        try {
            return userDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("获取用户列表失败");
        }
    }

    public List<User> getUsersByPage(int page, int limit) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getUsersByPage(page, limit);
        }
        
        try {
            return userDao.findByPage((page - 1) * limit, limit);
        } catch (Exception e) {
            throw new RuntimeException("获取用户列表失败");
        }
    }

    public int getUserCount() {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getUserCount();
        }
        
        try {
            return userDao.count();
        } catch (Exception e) {
            throw new RuntimeException("获取用户数量失败");
        }
    }

    public boolean updateUser(User user) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.updateUser(user);
        }
        
        try {
            return userDao.update(user) > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新用户信息失败");
        }
    }

    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.changePassword(userId, oldPassword, newPassword);
        }
        
        try {
            User user = userDao.findById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            if (!MD5Util.verify(oldPassword, user.getPassword())) {
                throw new RuntimeException("原密码错误");
            }
            return userDao.updatePassword(userId, MD5Util.md5(newPassword)) > 0;
        } catch (Exception e) {
            throw new RuntimeException("修改密码失败");
        }
    }

    public boolean deleteUser(Integer id) {
        if (AppConfig.USE_MOCK_DATA) {
            // MockData 中没有 deleteUser 方法，暂时返回 true
            return true;
        }
        
        try {
            return userDao.delete(id) > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除用户失败");
        }
    }

    public List<User> searchUsers(String keyword) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.searchUsers(keyword);
        }
        
        try {
            return userDao.search(keyword);
        } catch (Exception e) {
            throw new RuntimeException("搜索用户失败");
        }
    }
}