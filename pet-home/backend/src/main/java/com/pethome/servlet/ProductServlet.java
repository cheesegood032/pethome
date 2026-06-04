package com.pethome.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethome.model.Product;
import com.pethome.service.ProductService;
import com.pethome.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/product/*")
public class ProductServlet extends HttpServlet {
    private ProductService productService = new ProductService();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        resp.setContentType("application/json;charset=UTF-8");

        try {
            if ("/add".equals(pathInfo)) {
                addProduct(req, resp);
            } else if ("/update".equals(pathInfo)) {
                updateProduct(req, resp);
            } else if ("/delete".equals(pathInfo)) {
                deleteProduct(req, resp);
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
                getProductList(req, resp);
            } else if ("/all".equals(pathInfo)) {
                getAllProducts(req, resp);
            } else if ("/search".equals(pathInfo)) {
                searchProducts(req, resp);
            } else if ("/detail".equals(pathInfo)) {
                getProductDetail(req, resp);
            } else {
                resp.getWriter().write(mapper.writeValueAsString(Result.error(404, "接口不存在")));
            }
        } catch (Exception e) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(e.getMessage())));
        }
    }

    private void getProductList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pageStr = req.getParameter("page");
        String limitStr = req.getParameter("limit");
        int page = pageStr == null ? 1 : Integer.parseInt(pageStr);
        int limit = limitStr == null ? 10 : Integer.parseInt(limitStr);

        List<Product> products = productService.getProductsByPage(page, limit);
        int total = productService.getProductCount();

        Map<String, Object> data = new HashMap<>();
        data.put("list", products);
        data.put("total", total);
        data.put("page", page);
        data.put("limit", limit);

        resp.getWriter().write(mapper.writeValueAsString(Result.success(data)));
    }

    private void getAllProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Product> products = productService.getAllProducts();
        resp.getWriter().write(mapper.writeValueAsString(Result.success(products)));
    }

    private void searchProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("keyword");
        String category = req.getParameter("category");
        String petType = req.getParameter("petType");

        List<Product> products = productService.searchProducts(keyword, category, petType);
        resp.getWriter().write(mapper.writeValueAsString(Result.success(products)));
    }

    private void getProductDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("商品ID不能为空")));
            return;
        }

        Product product = productService.getProductById(Integer.parseInt(idStr));
        if (product == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("商品不存在")));
            return;
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success(product)));
    }

    private void addProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Product product = new Product();
        product.setName(req.getParameter("name"));
        product.setCategory(req.getParameter("category"));
        product.setPetType(req.getParameter("petType"));
        product.setPrice(new BigDecimal(req.getParameter("price")));
        product.setStock(Integer.parseInt(req.getParameter("stock")));
        product.setImage(req.getParameter("image"));
        product.setDescription(req.getParameter("description"));
        product.setSpec(req.getParameter("spec"));
        product.setStatus(1);

        if (productService.addProduct(product)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("添加成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("添加失败")));
        }
    }

    private void updateProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Product product = new Product();
        product.setId(Integer.parseInt(req.getParameter("id")));
        product.setName(req.getParameter("name"));
        product.setCategory(req.getParameter("category"));
        product.setPetType(req.getParameter("petType"));
        product.setPrice(new BigDecimal(req.getParameter("price")));
        product.setStock(Integer.parseInt(req.getParameter("stock")));
        product.setImage(req.getParameter("image"));
        product.setDescription(req.getParameter("description"));
        product.setSpec(req.getParameter("spec"));
        product.setStatus(Integer.parseInt(req.getParameter("status")));

        if (productService.updateProduct(product)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("更新成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("更新失败")));
        }
    }

    private void deleteProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (productService.deleteProduct(Integer.parseInt(idStr))) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("删除成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("删除失败")));
        }
    }

    private void updateStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        String statusStr = req.getParameter("status");

        if (productService.updateStatus(Integer.parseInt(idStr), Integer.parseInt(statusStr))) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("状态更新成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("状态更新失败")));
        }
    }
}
