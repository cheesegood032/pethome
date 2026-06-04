package com.pethome.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethome.model.FosterPackage;
import com.pethome.service.FosterPackageService;
import com.pethome.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/api/foster/package/*")
public class FosterPackageServlet extends HttpServlet {
    private FosterPackageService packageService = new FosterPackageService();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        resp.setContentType("application/json;charset=UTF-8");

        try {
            if ("/add".equals(pathInfo)) {
                addPackage(req, resp);
            } else if ("/update".equals(pathInfo)) {
                updatePackage(req, resp);
            } else if ("/delete".equals(pathInfo)) {
                deletePackage(req, resp);
            } else if ("/updateStatus".equals(pathInfo)) {
                updateStatus(req, resp);
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
                getPackageList(req, resp);
            } else if ("/all".equals(pathInfo)) {
                getAllPackages(req, resp);
            } else if ("/detail".equals(pathInfo)) {
                getPackageDetail(req, resp);
            } else {
                resp.getWriter().write(mapper.writeValueAsString(Result.error(404, "接口不存在")));
            }
        } catch (Exception e) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(e.getMessage())));
        }
    }

    private void getPackageList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<?> packages = packageService.getPackagesByStatus(1);
        resp.getWriter().write(mapper.writeValueAsString(Result.success(packages)));
    }

    private void getAllPackages(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<?> packages = packageService.getAllPackages();
        resp.getWriter().write(mapper.writeValueAsString(Result.success(packages)));
    }

    private void getPackageDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        FosterPackage pkg = packageService.getPackageById(Integer.parseInt(idStr));
        if (pkg == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("套餐不存在")));
            return;
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success(pkg)));
    }

    private void addPackage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        FosterPackage pkg = new FosterPackage();
        pkg.setName(req.getParameter("name"));
        pkg.setDescription(req.getParameter("description"));
        pkg.setPricePerDay(new BigDecimal(req.getParameter("pricePerDay")));
        pkg.setServices(req.getParameter("services"));
        pkg.setStatus(1);

        if (packageService.addPackage(pkg)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("添加成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("添加失败")));
        }
    }

    private void updatePackage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        FosterPackage pkg = new FosterPackage();
        pkg.setId(Integer.parseInt(req.getParameter("id")));
        pkg.setName(req.getParameter("name"));
        pkg.setDescription(req.getParameter("description"));
        pkg.setPricePerDay(new BigDecimal(req.getParameter("pricePerDay")));
        pkg.setServices(req.getParameter("services"));
        pkg.setStatus(Integer.parseInt(req.getParameter("status")));

        if (packageService.updatePackage(pkg)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("更新成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("更新失败")));
        }
    }

    private void deletePackage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (packageService.deletePackage(Integer.parseInt(idStr))) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("删除成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("删除失败")));
        }
    }

    private void updateStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        String statusStr = req.getParameter("status");

        if (packageService.updateStatus(Integer.parseInt(idStr), Integer.parseInt(statusStr))) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("状态更新成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("状态更新失败")));
        }
    }
}
