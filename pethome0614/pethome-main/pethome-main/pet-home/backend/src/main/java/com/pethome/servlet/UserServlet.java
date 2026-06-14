package com.pethome.servlet;

import com.pethome.dao.UserDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户模块 Servlet
 * URL映射: /api/user/*
 * 接口: register, login, logout, info, update, changePassword
 */
public class UserServlet extends BaseServlet {

    private UserDAO userDAO = new UserDAO();

    /**
     * POST /api/user/register
     * 注册（username/password/phone/address，校验用户名和手机号不重复）
     */
    protected void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String phone = (String) body.get("phone");
        String address = (String) body.get("address");

        // 参数校验
        if (username == null || username.trim().isEmpty()) {
            renderError(resp, 400, "用户名不能为空");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            renderError(resp, 400, "密码不能为空");
            return;
        }
        if (phone == null || phone.trim().isEmpty()) {
            renderError(resp, 400, "手机号不能为空");
            return;
        }

        // 校验用户名不重复
        if (userDAO.findByUsername(username.trim()) != null) {
            renderError(resp, 400, "用户名已存在");
            return;
        }
        // 校验手机号不重复
        if (userDAO.findByPhone(phone.trim()) != null) {
            renderError(resp, 400, "手机号已被注册");
            return;
        }

        long userId = userDAO.insert(username.trim(), password, phone.trim(),
                address != null ? address.trim() : "");
        if (userId > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", userId);
            renderSuccess(resp, data);
        } else {
            renderError(resp, 500, "注册失败");
        }
    }

    /**
     * POST /api/user/login
     * 登录（支持 username 或 phone + password）
     */
    protected void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        String username = (String) body.get("username");
        String password = (String) body.get("password");

        if (username == null || username.trim().isEmpty()) {
            renderError(resp, 400, "用户名/手机号不能为空");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            renderError(resp, 400, "密码不能为空");
            return;
        }

        username = username.trim();

        // 先按用户名查，查不到按手机号查
        Map<String, Object> user = userDAO.findByUsername(username);
        if (user == null) {
            user = userDAO.findByPhone(username);
        }

        if (user == null) {
            renderError(resp, 400, "用户不存在");
            return;
        }

        if (!password.equals(user.get("password"))) {
            renderError(resp, 400, "密码错误");
            return;
        }

        // 登录成功，写入 Session
        HttpSession session = req.getSession(true);
        user.remove("password"); // 不存密码到 session
        session.setAttribute("loginUser", user);

        renderSuccess(resp, user);
    }

    /**
     * POST /api/user/logout
     * 退出登录
     */
    protected void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.removeAttribute("loginUser");
            session.invalidate();
        }
        renderSuccess(resp, null);
    }

    /**
     * GET /api/user/info
     * 获取当前登录用户信息
     */
    protected void info(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            renderError(resp, 401, "请先登录");
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> sessionUser = (Map<String, Object>) session.getAttribute("loginUser");
        // 从数据库重新查询最新信息
        long userId = ((Number) sessionUser.get("id")).longValue();
        Map<String, Object> user = userDAO.findById(userId);
        if (user == null) {
            renderError(resp, 400, "用户不存在");
            return;
        }
        user.remove("password"); // 不返回密码
        renderSuccess(resp, user);
    }

    /**
     * POST /api/user/update
     * 修改个人信息（phone/address/email/avatar）
     */
    protected void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        @SuppressWarnings("unchecked")
        Map<String, Object> sessionUser = (Map<String, Object>) session.getAttribute("loginUser");
        long userId = ((Number) sessionUser.get("id")).longValue();

        Map<String, Object> body = parseBody(req);

        // 如果要修改密码，单独处理
        String password = (String) body.get("password");
        if (password != null && !password.trim().isEmpty()) {
            userDAO.updatePassword(userId, password);
        }

        // 修改其他字段
        body.remove("password");
        body.remove("id");
        body.remove("username"); // 用户名不可修改
        body.remove("create_time");

        if (!body.isEmpty()) {
            // 手机号唯一性校验
            if (body.containsKey("phone")) {
                String newPhone = (String) body.get("phone");
                if (newPhone != null && !newPhone.trim().isEmpty()) {
                    Map<String, Object> existing = userDAO.findByPhone(newPhone.trim());
                    if (existing != null && !existing.get("id").equals(sessionUser.get("id"))) {
                        renderError(resp, 400, "手机号已被其他用户使用");
                        return;
                    }
                }
            }
            userDAO.updateInfo(userId, body);
        }

        // 更新 session 中的用户信息
        Map<String, Object> updatedUser = userDAO.findById(userId);
        updatedUser.remove("password");
        session.setAttribute("loginUser", updatedUser);

        renderSuccess(resp, updatedUser);
    }

    /**
     * POST /api/user/changePassword
     * 修改密码
     */
    protected void changePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        @SuppressWarnings("unchecked")
        Map<String, Object> sessionUser = (Map<String, Object>) session.getAttribute("loginUser");
        long userId = ((Number) sessionUser.get("id")).longValue();

        Map<String, Object> body = parseBody(req);
        String oldPassword = (String) body.get("oldPassword");
        String newPassword = (String) body.get("newPassword");

        if (newPassword == null || newPassword.trim().isEmpty()) {
            renderError(resp, 400, "新密码不能为空");
            return;
        }

        // 验证旧密码
        Map<String, Object> user = userDAO.findById(userId);
        if (oldPassword != null && !oldPassword.equals(user.get("password"))) {
            renderError(resp, 400, "原密码错误");
            return;
        }

        userDAO.updatePassword(userId, newPassword);
        renderSuccess(resp, null);
    }
}
