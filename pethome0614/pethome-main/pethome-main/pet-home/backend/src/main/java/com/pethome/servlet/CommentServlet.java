package com.pethome.servlet;

import com.pethome.dao.CommentDAO;
import com.pethome.dao.OrderDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 用户评论模块 Servlet
 * URL映射: /api/comment/*
 *
 * 接口：
 *   POST /api/comment/add     — 用户提交评论（需登录）
 *   GET  /api/comment/list    — 按商品查询评论（无需登录）
 *   GET  /api/comment/status  — 查询某订单下已评论的订单项
 */
public class CommentServlet extends BaseServlet {

    private CommentDAO commentDAO = new CommentDAO();
    private OrderDAO orderDAO = new OrderDAO();

    private long getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return -1;
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("loginUser");
        if (user == null) return -1;
        return ((Number) user.get("id")).longValue();
    }

    /**
     * POST /api/comment/add — 提交评论
     * body: { orderItemId, productId, rating, content }
     */
    protected void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = getUserId(req);
        if (userId < 0) {
            renderError(resp, 401, "请先登录");
            return;
        }

        Map<String, Object> body = parseBody(req);

        // 解析参数
        Object oiObj = body.get("orderItemId");
        if (oiObj == null) oiObj = body.get("order_item_id");
        Object piObj = body.get("productId");
        if (piObj == null) piObj = body.get("product_id");

        if (oiObj == null || piObj == null) {
            renderError(resp, 400, "参数不完整");
            return;
        }

        long orderItemId = ((Number) oiObj).longValue();
        long productId = ((Number) piObj).longValue();

        Object ratingObj = body.get("rating");
        int rating = ratingObj != null ? ((Number) ratingObj).intValue() : 5;
        if (rating < 1) rating = 1;
        if (rating > 5) rating = 5;

        String content = (String) body.get("content");
        if (content == null || content.trim().isEmpty()) {
            renderError(resp, 400, "评论内容不能为空");
            return;
        }

        // 检查是否已评论
        if (commentDAO.hasCommented(orderItemId)) {
            renderError(resp, 400, "该商品已经评论过了");
            return;
        }

        long id = commentDAO.insert(orderItemId, productId, userId, rating, content.trim());
        if (id > 0) {
            renderSuccess(resp, id);
        } else {
            renderError(resp, 500, "评论提交失败");
        }
    }

    /**
     * GET /api/comment/list?productId=xxx — 按商品查询评论
     */
    protected void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long productId = getLongParam(req, "productId", 0);
        if (productId <= 0) productId = getLongParam(req, "product_id", 0);
        if (productId <= 0) {
            renderError(resp, 400, "商品ID不能为空");
            return;
        }
        List<Map<String, Object>> comments = commentDAO.findByProductId(productId);
        renderSuccess(resp, comments);
    }

    /**
     * GET /api/comment/status?orderId=xxx — 查询某订单下已评论的订单项ID列表
     */
    protected void status(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long orderId = getLongParam(req, "orderId", 0);
        if (orderId <= 0) orderId = getLongParam(req, "order_id", 0);
        if (orderId <= 0) {
            renderError(resp, 400, "订单ID不能为空");
            return;
        }
        List<Long> commentedItemIds = commentDAO.findCommentedItemIds(orderId);
        renderSuccess(resp, commentedItemIds);
    }
}
