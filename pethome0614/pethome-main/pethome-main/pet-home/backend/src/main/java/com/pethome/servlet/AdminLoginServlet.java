package com.pethome.servlet;

import com.pethome.dao.AdminDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * 管理员登录/登出 Servlet
 * URL映射: /api/admin/login, /api/admin/logout
 * 说明: AdminLoginFilter 排除 login 接口
 */
public class AdminLoginServlet extends BaseServlet {

    private AdminDAO adminDAO = new AdminDAO();

    /**
     * POST /api/admin/login
     * 支持 username 或 phone + password 登录
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
        Map<String, Object> admin = adminDAO.findByUsername(username);
        if (admin == null) {
            admin = adminDAO.findByPhone(username);
        }

        if (admin == null) {
            renderError(resp, 400, "管理员不存在");
            return;
        }

        if (!password.equals(admin.get("password"))) {
            renderError(resp, 400, "密码错误");
            return;
        }

        // 登录成功，写入 Session
        HttpSession session = req.getSession(true);
        admin.remove("password");
        session.setAttribute("loginAdmin", admin);

        renderSuccess(resp, admin);
    }

    /**
     * POST /api/admin/logout
     */
    protected void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.removeAttribute("loginAdmin");
            session.invalidate();
        }
        renderSuccess(resp, null);
    }

    /**
     * GET /api/admin/info — 获取当前管理员信息
     */
    protected void info(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginAdmin") == null) {
            renderError(resp, 401, "管理员未登录");
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> admin = (Map<String, Object>) session.getAttribute("loginAdmin");
        renderSuccess(resp, admin);
    }

    /**
     * GET /api/admin/stats — 获取 Dashboard 统计数据
     */
    protected void stats(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginAdmin") == null) {
            renderError(resp, 401, "管理员未登录");
            return;
        }
        Map<String, Object> stats = adminDAO.getDashboardStats();
        renderSuccess(resp, stats);
    }
}
