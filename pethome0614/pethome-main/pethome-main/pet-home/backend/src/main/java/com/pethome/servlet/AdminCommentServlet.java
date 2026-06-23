package com.pethome.servlet;

import com.pethome.dao.CommentDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 管理员评论管理 Servlet
 * URL映射: /api/admin/comment/*
 *
 * 接口：
 *   GET  /api/admin/comment/list   — 分页查询所有评论
 *   POST /api/admin/comment/reply  — 管理员回复评论
 */
public class AdminCommentServlet extends BaseServlet {

    private CommentDAO commentDAO = new CommentDAO();

    /**
     * GET /api/admin/comment/list?page=&pageSize=&keyword=
     */
    protected void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = getIntParam(req, "page", 1);
        int pageSize = getIntParam(req, "pageSize", 10);
        String keyword = req.getParameter("keyword");
        Map<String, Object> result = commentDAO.findAll(page, pageSize, keyword);
        renderSuccess(resp, result);
    }

    /**
     * POST /api/admin/comment/reply
     * body: { commentId, reply }
     */
    protected void reply(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);

        Object idObj = body.get("commentId");
        if (idObj == null) idObj = body.get("comment_id");
        if (idObj == null) idObj = body.get("id");
        if (idObj == null) {
            renderError(resp, 400, "评论ID不能为空");
            return;
        }
        long commentId = ((Number) idObj).longValue();

        String replyContent = (String) body.get("reply");
        if (replyContent == null || replyContent.trim().isEmpty()) {
            renderError(resp, 400, "回复内容不能为空");
            return;
        }

        int rows = commentDAO.reply(commentId, replyContent.trim());
        if (rows > 0) {
            renderSuccess(resp, null);
        } else {
            renderError(resp, 404, "评论不存在");
        }
    }
}
