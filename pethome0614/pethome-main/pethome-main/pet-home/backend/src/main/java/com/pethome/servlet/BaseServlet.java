package com.pethome.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有业务 Servlet 的父类
 * 根据 URL pathInfo 自动分发到子类对应方法
 */
public abstract class BaseServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 统一设置编码和响应类型
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");

        // 获取路径信息，如 /login, /list
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            renderError(resp, 404, "接口不存在");
            return;
        }

        // 去掉开头的 /，得到方法名，如 login, list
        String methodName = pathInfo.substring(1);
        // 如果路径中还有 /，只取第一段
        int slashIdx = methodName.indexOf('/');
        if (slashIdx > 0) {
            methodName = methodName.substring(0, slashIdx);
        }

        // Java 保留字映射
        if ("new".equals(methodName)) {
            methodName = "newList";
        }

        try {
            // 用反射调用子类中对应方法
            Method method = this.getClass().getDeclaredMethod(
                    methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.setAccessible(true);
            method.invoke(this, req, resp);
        } catch (NoSuchMethodException e) {
            renderError(resp, 404, "接口不存在: " + methodName);
        } catch (Exception e) {
            e.printStackTrace();
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            renderError(resp, 500, "服务器内部错误: " + cause.getMessage());
        }
    }

    /**
     * 返回成功响应 {"code":200,"msg":"ok","data":...}
     */
    protected void renderSuccess(HttpServletResponse resp, Object data) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "ok");
        result.put("data", data);
        writeJson(resp, result);
    }

    /**
     * 返回错误响应 {"code":xxx,"msg":"..."}
     */
    protected void renderError(HttpServletResponse resp, int code, String msg) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("msg", msg);
        writeJson(resp, result);
    }

    /**
     * 读取请求体 JSON 并转为 Map
     */
    protected Map<String, Object> parseBody(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String body = sb.toString().trim();
        if (body.isEmpty()) {
            return new HashMap<>();
        }
        JSONObject json = JSON.parseObject(body);
        return new HashMap<>(json);
    }

    /**
     * 将对象序列化为 JSON 并写入响应
     */
    private void writeJson(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.print(JSON.toJSONString(obj));
        out.flush();
    }

    /**
     * 从请求参数中获取 int 值，带默认值
     */
    protected int getIntParam(HttpServletRequest req, String name, int defaultValue) {
        String val = req.getParameter(name);
        if (val == null || val.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 从请求参数中获取 long 值，带默认值
     */
    protected long getLongParam(HttpServletRequest req, String name, long defaultValue) {
        String val = req.getParameter(name);
        if (val == null || val.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
