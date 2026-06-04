package com.pethome.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethome.service.CartService;
import com.pethome.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/cart/*")
public class CartServlet extends HttpServlet {
    private CartService cartService = new CartService();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        resp.setContentType("application/json;charset=UTF-8");

        try {
            if ("/add".equals(pathInfo)) {
                addToCart(req, resp);
            } else if ("/update".equals(pathInfo)) {
                updateQuantity(req, resp);
            } else if ("/remove".equals(pathInfo)) {
                removeFromCart(req, resp);
            } else if ("/clear".equals(pathInfo)) {
                clearCart(req, resp);
            } else {
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
            if ("/list".equals(pathInfo)) {
                getCartList(req, resp);
            } else {
                resp.getWriter().write(mapper.writeValueAsString(Result.error(404, "接口不存在")));
            }
        } catch (Exception e) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(e.getMessage())));
        }
    }

    private void getCartList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        List<?> carts = cartService.getCartByUserId(userId);
        resp.getWriter().write(mapper.writeValueAsString(Result.success(carts)));
    }

    private void addToCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        String productIdStr = req.getParameter("productId");
        String quantityStr = req.getParameter("quantity");
        String spec = req.getParameter("spec");

        if (productIdStr == null || quantityStr == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("参数不完整")));
            return;
        }

        if (cartService.addToCart(userId, Integer.parseInt(productIdStr),
                Integer.parseInt(quantityStr), spec)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("添加成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("添加失败")));
        }
    }

    private void updateQuantity(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String cartIdStr = req.getParameter("cartId");
        String quantityStr = req.getParameter("quantity");

        if (cartService.updateQuantity(Integer.parseInt(cartIdStr), Integer.parseInt(quantityStr))) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("更新成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("更新失败")));
        }
    }

    private void removeFromCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String cartIdStr = req.getParameter("cartId");

        if (cartService.removeFromCart(Integer.parseInt(cartIdStr))) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("删除成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("删除失败")));
        }
    }

    private void clearCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        cartService.clearCart(userId);
        resp.getWriter().write(mapper.writeValueAsString(Result.success("清空成功")));
    }
}
