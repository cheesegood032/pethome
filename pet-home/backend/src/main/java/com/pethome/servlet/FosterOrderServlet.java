package com.pethome.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethome.service.FosterOrderService;
import com.pethome.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/api/foster/order/*")
public class FosterOrderServlet extends HttpServlet {
    private FosterOrderService orderService = new FosterOrderService();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        resp.setContentType("application/json;charset=UTF-8");

        try {
            if ("/create".equals(pathInfo)) {
                createOrder(req, resp);
            } else if ("/cancel".equals(pathInfo)) {
                cancelOrder(req, resp);
            } else if ("/audit".equals(pathInfo)) {
                auditOrder(req, resp);
            } else if ("/complete".equals(pathInfo)) {
                completeOrder(req, resp);
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
            } else if ("/all".equals(pathInfo)) {
                getAllOrders(req, resp);
            } else if ("/search".equals(pathInfo)) {
                searchOrders(req, resp);
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
        String idStr = req.getParameter("id");
        Object order = orderService.getOrderById(Integer.parseInt(idStr));
        if (order == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("订单不存在")));
            return;
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success(order)));
    }

    private void getAllOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String statusStr = req.getParameter("status");
        List<?> orders;
        if (statusStr != null) {
            orders = orderService.getOrdersByStatus(Integer.parseInt(statusStr));
        } else {
            orders = orderService.getAllOrders();
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success(orders)));
    }

    private void createOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        String petIdStr = req.getParameter("petId");
        String packageIdStr = req.getParameter("packageId");
        String startDateStr = req.getParameter("startDate");
        String endDateStr = req.getParameter("endDate");
        String remark = req.getParameter("remark");

        if (petIdStr == null || packageIdStr == null || startDateStr == null || endDateStr == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("参数不完整")));
            return;
        }

        Object order = orderService.createOrder(userId, Integer.parseInt(petIdStr),
                Integer.parseInt(packageIdStr), Date.valueOf(startDateStr), Date.valueOf(endDateStr), remark);
        resp.getWriter().write(mapper.writeValueAsString(Result.success("预约成功", order)));
    }

    private void cancelOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        String orderIdStr = req.getParameter("orderId");
        if (orderService.cancelOrder(Integer.parseInt(orderIdStr), userId)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("取消成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("取消失败")));
        }
    }

    private void auditOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderIdStr = req.getParameter("orderId");
        String approvedStr = req.getParameter("approved");
        String rejectReason = req.getParameter("rejectReason");

        boolean approved = "true".equals(approvedStr) || "1".equals(approvedStr);
        if (orderService.auditOrder(Integer.parseInt(orderIdStr), approved, rejectReason)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("审核完成")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("审核失败")));
        }
    }

    private void completeOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderIdStr = req.getParameter("orderId");
        if (orderService.completeOrder(Integer.parseInt(orderIdStr))) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("操作成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("操作失败")));
        }
    }

    private void searchOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userIdStr = req.getParameter("userId");
        String statusStr = req.getParameter("status");
        String startDateStr = req.getParameter("startDate");
        String endDateStr = req.getParameter("endDate");

        Integer userId = userIdStr != null ? Integer.parseInt(userIdStr) : null;
        Integer status = statusStr != null ? Integer.parseInt(statusStr) : null;
        Date startDate = startDateStr != null ? Date.valueOf(startDateStr) : null;
        Date endDate = endDateStr != null ? Date.valueOf(endDateStr) : null;

        List<?> orders = orderService.searchOrders(userId, status, startDate, endDate);
        resp.getWriter().write(mapper.writeValueAsString(Result.success(orders)));
    }
}
