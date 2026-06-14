package com.pethome.servlet;

import com.pethome.dao.StoreDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 门店模块 Servlet
 * URL映射: /api/store/*
 */
public class StoreServlet extends BaseServlet {

    private StoreDAO storeDAO = new StoreDAO();

    /**
     * GET /api/store/list — 获取所有启用门店
     */
    protected void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        renderSuccess(resp, storeDAO.findAll());
    }
}
