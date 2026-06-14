package com.pethome.servlet;

import com.pethome.dao.PetDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 宠物模块 Servlet（用户端）
 * URL映射: /api/pet/*
 */
public class PetServlet extends BaseServlet {

    private PetDAO petDAO = new PetDAO();

    private long getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("loginUser");
        return ((Number) user.get("id")).longValue();
    }

    protected void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        List<Map<String, Object>> list = petDAO.findByUserId(userId);
        renderSuccess(resp, list);
    }

    protected void all(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        list(req, resp);
    }

    protected void detail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = getLongParam(req, "id", 0);
        if (id <= 0) { renderError(resp, 400, "宠物ID不能为空"); return; }
        Map<String, Object> pet = petDAO.findById(id);
        if (pet == null) { renderError(resp, 404, "宠物不存在"); return; }
        renderSuccess(resp, pet);
    }

    protected void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        Map<String, Object> body = parseBody(req);
        String name = (String) body.get("name");
        if (name == null || name.trim().isEmpty()) { renderError(resp, 400, "宠物名称不能为空"); return; }

        long id = petDAO.insert(userId, name.trim(),
                (String) body.getOrDefault("species", ""),
                (String) body.getOrDefault("breed", ""),
                body.get("age") != null ? body.get("age").toString() : "",
                (String) body.getOrDefault("gender", ""),
                body.get("weight") != null ? body.get("weight").toString() : "",
                (String) body.getOrDefault("image", ""),
                (String) body.getOrDefault("health_status", "健康"),
                (String) body.getOrDefault("remark", ""));

        java.util.HashMap<String, Object> data = new java.util.HashMap<>();
        data.put("id", id);
        renderSuccess(resp, data);
    }

    protected void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        Map<String, Object> body = parseBody(req);
        Object idObj = body.get("id");
        if (idObj == null) { renderError(resp, 400, "宠物ID不能为空"); return; }
        long id = ((Number) idObj).longValue();
        body.remove("id");
        body.remove("user_id");
        body.remove("create_time");
        body.remove("update_time");
        petDAO.update(id, userId, body);
        renderSuccess(resp, null);
    }

    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        long id = getLongParam(req, "id", 0);
        if (id <= 0) {
            Map<String, Object> body = parseBody(req);
            Object idObj = body.get("id");
            if (idObj != null) id = ((Number) idObj).longValue();
        }
        if (id <= 0) { renderError(resp, 400, "宠物ID不能为空"); return; }
        petDAO.delete(id, userId);
        renderSuccess(resp, null);
    }
}
