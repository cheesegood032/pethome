package com.pethome.servlet;

import com.pethome.dao.ProductDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 商品模块 Servlet
 * URL映射: /api/product/*
 */
public class ProductServlet extends BaseServlet {

    private ProductDAO productDAO = new ProductDAO();

    /**
     * GET /api/product/list
     * 商品列表（分页，支持 keyword/pet_type/category 筛选）
     */
    protected void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("keyword");
        String petType = req.getParameter("pet_type");
        String category = req.getParameter("category");
        int page = getIntParam(req, "page", 1);
        int pageSize = getIntParam(req, "pageSize", 10);
        Map<String, Object> result = productDAO.findList(keyword, petType, category, page, pageSize);
        renderSuccess(resp, result);
    }

    /**
     * GET /api/product/detail?id=
     */
    protected void detail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = getLongParam(req, "id", 0);
        if (id <= 0) {
            renderError(resp, 400, "商品ID不能为空");
            return;
        }
        Map<String, Object> product = productDAO.findById(id);
        if (product == null) {
            renderError(resp, 404, "商品不存在");
            return;
        }
        renderSuccess(resp, product);
    }

    /**
     * GET /api/product/hot — 热销Top5
     */
    protected void hot(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> list = productDAO.findHot();
        renderSuccess(resp, list);
    }

    /**
     * GET /api/product/new — 新品上架Top8
     */
    @SuppressWarnings("unused")
    protected void newList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> list = productDAO.findNew();
        renderSuccess(resp, list);
    }

    /**
     * GET /api/product/all — 全部上架商品
     */
    protected void all(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> list = productDAO.findAll();
        renderSuccess(resp, list);
    }

    /**
     * GET /api/product/search — 搜索（复用 list 的逻辑）
     */
    protected void search(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        list(req, resp);
    }
}
