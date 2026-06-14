package com.pethome.filter;

import com.alibaba.fastjson.JSON;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录过滤器 —— 保护需要用户登录的接口
 * 检查 Session 中是否有 "loginUser" 属性
 */
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // OPTIONS 请求直接放行
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("loginUser") != null) {
            // 已登录，放行
            chain.doFilter(servletRequest, servletResponse);
        } else {
            // 未登录，返回 401
            resp.setContentType("application/json;charset=utf-8");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("msg", "请先登录");
            PrintWriter out = resp.getWriter();
            out.print(JSON.toJSONString(result));
            out.flush();
        }
    }

    @Override
    public void destroy() {
    }
}
