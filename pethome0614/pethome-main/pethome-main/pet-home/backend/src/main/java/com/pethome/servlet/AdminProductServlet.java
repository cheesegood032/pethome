package com.pethome.servlet;

import com.pethome.dao.ProductDAO;
import com.pethome.util.DBUtil;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * 后台商品管理 Servlet
 * URL映射: /api/admin/product/*
 */
public class AdminProductServlet extends BaseServlet {

    private ProductDAO productDAO = new ProductDAO();

    /**
     * GET /api/admin/product/list
     * 多条件查询（keyword/pet_type/category/status，分页）
     */
    protected void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("keyword");
        String petType = req.getParameter("pet_type");
        String category = req.getParameter("category");
        String statusStr = req.getParameter("status");
        int page = getIntParam(req, "page", 1);
        int pageSize = getIntParam(req, "pageSize", 10);

        // 管理端查询支持所有 status
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            where.append(" AND name LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }
        if (petType != null && !petType.trim().isEmpty()) {
            where.append(" AND pet_type = ?");
            params.add(petType.trim());
        }
        if (category != null && !category.trim().isEmpty()) {
            where.append(" AND category = ?");
            params.add(category.trim());
        }
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            where.append(" AND status = ?");
            params.add(Integer.parseInt(statusStr.trim()));
        }

        String countSql = "SELECT COUNT(*) FROM product" + where;
        String dataSql = "SELECT * FROM product" + where
                + " ORDER BY create_time DESC LIMIT ?, ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();

            ps = conn.prepareStatement(countSql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            rs = ps.executeQuery();
            int total = 0;
            if (rs.next()) total = rs.getInt(1);
            rs.close();
            ps.close();

            ps = conn.prepareStatement(dataSql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.setInt(params.size() + 1, (page - 1) * pageSize);
            ps.setInt(params.size() + 2, pageSize);
            rs = ps.executeQuery();

            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                list.add(toMap(rs));
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("list", list);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            renderSuccess(resp, result);
        } catch (SQLException e) {
            throw new RuntimeException("查询商品列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * POST /api/admin/product/add
     * 新增商品（multipart/form-data，含图片上传）
     */
    protected void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Map<String, String> fields = new HashMap<>();
            String imagePath = null;

            if (JakartaServletFileUpload.isMultipartContent(req)) {
                DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
                upload.setHeaderCharset(StandardCharsets.UTF_8);
                List<FileItem> items = upload.parseRequest(req);

                for (FileItem item : items) {
                    if (item.isFormField()) {
                        fields.put(item.getFieldName(), item.getString(StandardCharsets.UTF_8));
                    } else {
                        if (item.getSize() > 0) {
                            imagePath = saveImage(req, item, "goods");
                        }
                    }
                }
            } else {
                // JSON 方式提交
                Map<String, Object> body = parseBody(req);
                for (Map.Entry<String, Object> e : body.entrySet()) {
                    fields.put(e.getKey(), e.getValue() != null ? e.getValue().toString() : "");
                }
            }

            String name = fields.get("name");
            String category = fields.get("category");
            String petType = fields.getOrDefault("pet_type", "");
            String priceStr = fields.getOrDefault("price", "0");
            String stockStr = fields.getOrDefault("stock", "0");
            String description = fields.getOrDefault("description", "");
            String spec = fields.getOrDefault("spec", "");

            if (name == null || name.trim().isEmpty()) {
                renderError(resp, 400, "商品名称不能为空");
                return;
            }

            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            String sql = "INSERT INTO product(name, category, pet_type, price, stock, sales_count, "
                    + "image, description, spec, status, create_time, update_time) "
                    + "VALUES(?,?,?,?,?,0,?,?,?,1,NOW(),NOW())";
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = DBUtil.getConnection();
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, name.trim());
                ps.setString(2, category);
                ps.setString(3, petType);
                ps.setDouble(4, price);
                ps.setInt(5, stock);
                ps.setString(6, imagePath != null ? imagePath : "");
                ps.setString(7, description);
                ps.setString(8, spec);
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                long id = rs.next() ? rs.getLong(1) : -1;

                Map<String, Object> data = new HashMap<>();
                data.put("id", id);
                renderSuccess(resp, data);
            } finally {
                DBUtil.close(conn, ps, rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderError(resp, 500, "新增商品失败: " + e.getMessage());
        }
    }

    /**
     * POST /api/admin/product/update
     * 修改商品信息（可含图片替换）
     */
    protected void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Map<String, String> fields = new HashMap<>();
            String imagePath = null;

            if (JakartaServletFileUpload.isMultipartContent(req)) {
                DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
                upload.setHeaderCharset(StandardCharsets.UTF_8);
                List<FileItem> items = upload.parseRequest(req);

                for (FileItem item : items) {
                    if (item.isFormField()) {
                        fields.put(item.getFieldName(), item.getString(StandardCharsets.UTF_8));
                    } else {
                        if (item.getSize() > 0) {
                            imagePath = saveImage(req, item, "goods");
                        }
                    }
                }
            } else {
                Map<String, Object> body = parseBody(req);
                for (Map.Entry<String, Object> e : body.entrySet()) {
                    fields.put(e.getKey(), e.getValue() != null ? e.getValue().toString() : "");
                }
            }

            String idStr = fields.get("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                renderError(resp, 400, "商品ID不能为空");
                return;
            }
            long id = Long.parseLong(idStr.trim());

            StringBuilder sb = new StringBuilder("UPDATE product SET ");
            List<Object> params = new ArrayList<>();
            String[] allowed = {"name", "category", "pet_type", "price", "stock", "description", "spec", "status"};
            boolean first = true;
            for (String key : allowed) {
                if (fields.containsKey(key)) {
                    if (!first) sb.append(", ");
                    sb.append(key).append(" = ?");
                    String val = fields.get(key);
                    if ("price".equals(key)) {
                        params.add(Double.parseDouble(val));
                    } else if ("stock".equals(key) || "status".equals(key)) {
                        params.add(Integer.parseInt(val));
                    } else {
                        params.add(val);
                    }
                    first = false;
                }
            }
            if (imagePath != null) {
                if (!first) sb.append(", ");
                sb.append("image = ?");
                params.add(imagePath);
                first = false;
            }
            if (params.isEmpty()) {
                renderError(resp, 400, "没有可更新的字段");
                return;
            }
            sb.append(", update_time = NOW() WHERE id = ?");
            params.add(id);

            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = DBUtil.getConnection();
                ps = conn.prepareStatement(sb.toString());
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                ps.executeUpdate();
                renderSuccess(resp, null);
            } finally {
                DBUtil.close(conn, ps);
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderError(resp, 500, "修改商品失败: " + e.getMessage());
        }
    }

    /**
     * POST /api/admin/product/delete
     * 软删除（status 改为 0）
     */
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        Object idObj = body.get("product_id");
        if (idObj == null) idObj = body.get("id");
        if (idObj == null) {
            renderError(resp, 400, "商品ID不能为空");
            return;
        }
        long id = ((Number) idObj).longValue();

        String sql = "UPDATE product SET status = 0, update_time = NOW() WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ps.executeUpdate();
            renderSuccess(resp, null);
        } catch (SQLException e) {
            throw new RuntimeException("删除商品失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 保存上传图片
     */
    private String saveImage(HttpServletRequest req, FileItem fileItem, String subDir) throws Exception {
        String uploadDir = req.getServletContext().getRealPath("/uploads/" + subDir);
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String originalName = fileItem.getName();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = System.currentTimeMillis() + "_" + ((int) (Math.random() * 9000) + 1000) + ext;
        File targetFile = new File(dir, fileName);
        fileItem.write(targetFile.toPath());

        return "/uploads/" + subDir + "/" + fileName;
    }

    private Map<String, Object> toMap(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            map.put(meta.getColumnLabel(i), rs.getObject(i));
        }
        return map;
    }
}
