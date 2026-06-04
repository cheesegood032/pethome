package com.pethome.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethome.model.User;
import com.pethome.service.UserService;
import com.pethome.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {
    private UserService userService = new UserService();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        resp.setContentType("application/json;charset=UTF-8");

        try {
            switch (pathInfo) {
                case "/register":
                    register(req, resp);
                    break;
                case "/login":
                    login(req, resp);
                    break;
                case "/logout":
                    logout(req, resp);
                    break;
                case "/update":
                    update(req, resp);
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
                getUserInfo(req, resp);
            } else if ("/list".equals(pathInfo)) {
                getUserList(req, resp);
            } else if ("/search".equals(pathInfo)) {
                searchUsers(req, resp);
            } else {
                resp.getWriter().write(mapper.writeValueAsString(Result.error(404, "接口不存在")));
            }
        } catch (Exception e) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(e.getMessage())));
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("用户名和密码不能为空")));
            return;
        }

        User user = userService.register(username.trim(), password, phone, email);
        user.setPassword(null);
        resp.getWriter().write(mapper.writeValueAsString(Result.success("注册成功", user)));
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String loginName = req.getParameter("loginName");
        String password = req.getParameter("password");

        if (loginName == null || loginName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("账号和密码不能为空")));
            return;
        }

        User user = userService.login(loginName.trim(), password);
        HttpSession session = req.getSession();
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("token", session.getId());
        resp.getWriter().write(mapper.writeValueAsString(Result.success("登录成功", data)));
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success("退出登录成功")));
    }

    private void getUserInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("用户不存在")));
            return;
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success(user)));
    }

    private void getUserList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pageStr = req.getParameter("page");
        String limitStr = req.getParameter("limit");
        int page = pageStr == null ? 1 : Integer.parseInt(pageStr);
        int limit = limitStr == null ? 10 : Integer.parseInt(limitStr);

        List<User> users = userService.getUsersByPage(page, limit);
        int total = userService.getUserCount();

        Map<String, Object> data = new HashMap<>();
        data.put("list", users);
        data.put("total", total);
        data.put("page", page);
        data.put("limit", limit);

        resp.getWriter().write(mapper.writeValueAsString(Result.success(data)));
    }

    private void searchUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("keyword");
        List<User> users = userService.searchUsers(keyword);
        resp.getWriter().write(mapper.writeValueAsString(Result.success(users)));
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        User user = userService.getUserById(userId);
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String address = req.getParameter("address");

        user.setPhone(phone);
        user.setEmail(email);
        user.setAddress(address);

        if (userService.updateUser(user)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("更新成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("更新失败")));
        }
    }

    private void changePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");

        if (userService.changePassword(userId, oldPassword, newPassword)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("密码修改成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("密码修改失败")));
        }
    }
}
