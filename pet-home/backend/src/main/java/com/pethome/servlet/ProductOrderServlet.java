package com.pethome.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethome.service.ProductOrderService;
import com.pethome.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/api/order/*")
public class ProductOrderServlet extends HttpServlet {
    private ProductOrderService orderService = new ProductOrderService();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        resp.setContentType("application/json;charset=UTF-8");

        try {
            if ("/create".equals(pathInfo)) {
                createOrder(req, resp);
            } else if ("/pay".equals(pathInfo)) {
                payOrder(req, resp);
            } else if ("/ship".equals(pathInfo)) {
                shipOrder(req, resp);
            } else if ("/receive".equals(pathInfo)) {
                receiveOrder(req, resp);
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
                getOrderList(req, resp);
            } else if ("/detail".equals(pathInfo)) {
                getOrderDetail(req, resp);
            } else if ("/search".equals(pathInfo)) {
                searchOrders(req, resp);
            } else if ("/admin/list".equals(pathInfo)) {
                getAdminOrderList(req, resp);
            } else {
                resp.getWriter().write(mapper.writeValueAsString(Result.error(404, "接口不存在")));
            }
        } catch (Exception e) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(e.getMessage())));
        }
    }

    private void getOrderList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        String statusStr = req.getParameter("status");
        List<?> orders;
        if (statusStr != null) {
            orders = orderService.getOrdersByUserIdAndStatus(userId, Integer.parseInt(statusStr));
        } else {
            orders = orderService.getOrdersByUserId(userId);
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success(orders)));
    }

    private void getOrderDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderIdStr = req.getParameter("orderId");
        if (orderIdStr == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("订单ID不能为空")));
            return;
        }

        Object order = orderService.getOrderById(Integer.parseInt(orderIdStr));
        if (order == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("订单不存在")));
            return;
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success(order)));
    }

    private void createOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        String receiverName = req.getParameter("receiverName");
        String receiverPhone = req.getParameter("receiverPhone");
        String receiverAddress = req.getParameter("receiverAddress");
        String remark = req.getParameter("remark");
        String cartIdsStr = req.getParameter("cartIds");

        if (receiverName == null || receiverPhone == null || receiverAddress == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("收货信息不完整")));
            return;
        }

        List<Integer> cartIds = null;
        if (cartIdsStr != null && !cartIdsStr.isEmpty()) {
            String[] ids = cartIdsStr.split(",");
            cartIds = Arrays.asList(Arrays.stream(ids).map(Integer::parseInt).toArray(Integer[]::new));
        }

        Object order = orderService.createOrder(userId, receiverName, receiverPhone,
                receiverAddress, remark, cartIds);
        resp.getWriter().write(mapper.writeValueAsString(Result.success("下单成功", order)));
    }

    private void payOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderIdStr = req.getParameter("orderId");
        if (orderService.payOrder(Integer.parseInt(orderIdStr))) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("支付成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("支付失败")));
        }
    }

    private void shipOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderIdStr = req.getParameter("orderId");
        if (orderService.shipOrder(Integer.parseInt(orderIdStr))) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("发货成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("发货失败")));
        }
    }

    private void receiveOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        String orderIdStr = req.getParameter("orderId");
        if (orderService.receiveOrder(Integer.parseInt(orderIdStr))) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("确认收货成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("确认收货失败")));
        }
    }

    private void searchOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("keyword");
        String statusStr = req.getParameter("status");

        Integer status = statusStr != null ? Integer.parseInt(statusStr) : null;
        List<?> orders = orderService.searchOrders(keyword, status);
        resp.getWriter().write(mapper.writeValueAsString(Result.success(orders)));
    }

    private void getAdminOrderList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String statusStr = req.getParameter("status");
        String pageStr = req.getParameter("page");
        String limitStr = req.getParameter("limit");

        List<?> orders;
        if (statusStr != null) {
            orders = orderService.getOrdersByStatus(Integer.parseInt(statusStr));
        } else {
            int page = pageStr == null ? 1 : Integer.parseInt(pageStr);
            int limit = limitStr == null ? 10 : Integer.parseInt(limitStr);
            orders = orderService.getOrdersByPage(page, limit);
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success(orders)));
    }
}
