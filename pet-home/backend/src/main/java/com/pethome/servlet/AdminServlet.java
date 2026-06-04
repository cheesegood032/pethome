package com.pethome.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethome.model.Admin;
import com.pethome.service.AdminService;
import com.pethome.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {
    private AdminService adminService = new AdminService();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        resp.setContentType("application/json;charset=UTF-8");

        try {
            switch (pathInfo) {
                case "/login":
                    login(req, resp);
                    break;
                case "/logout":
                    logout(req, resp);
                    break;
                case "/changePassword":
                    changePassword(req, resp);
                    break;
                default:
                    resp.getWriter().write(mapper.writeValueAsString(Result.error(404, "接口不存在")));
            }
        } catch (Exception e) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(e.getMessage())));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        resp.setContentType("application/json;charset=UTF-8");

        try {
            if ("/info".equals(pathInfo)) {
                getAdminInfo(req, resp);
            } else {
                resp.getWriter().write(mapper.writeValueAsString(Result.error(404, "接口不存在")));
            }
        } catch (Exception e) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(e.getMessage())));
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String loginName = null;
        String password = null;
        
        String contentType = req.getContentType();
        
        // 尝试从表单参数获取
        loginName = req.getParameter("loginName");
        password = req.getParameter("password");
        
        // 如果表单参数为空，尝试从请求体获取（JSON格式）
        if ((loginName == null || password == null) && contentType != null && contentType.contains("application/json")) {
            try {
                Map<String, Object> body = mapper.readValue(req.getInputStream(), Map.class);
                loginName = (String) body.get("loginName");
                password = (String) body.get("password");
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        if (loginName == null || password == null || loginName.isEmpty() || password.isEmpty()) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("账号和密码不能为空")));
            return;
        }

        Admin admin = adminService.login(loginName, password);
        HttpSession session = req.getSession();
        session.setAttribute("admin", admin);
        session.setAttribute("adminId", admin.getId());

        resp.getWriter().write(mapper.writeValueAsString(Result.success("登录成功", admin)));
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success("退出登录成功")));
    }

    private void getAdminInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        Admin admin = adminService.getAdminById(adminId);
        if (admin == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("管理员不存在")));
            return;
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success(admin)));
    }

    private void changePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer adminId = (Integer) session.getAttribute("adminId");
        if (adminId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");

        if (adminService.changePassword(adminId, oldPassword, newPassword)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("密码修改成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("密码修改失败")));
        }
    }
}