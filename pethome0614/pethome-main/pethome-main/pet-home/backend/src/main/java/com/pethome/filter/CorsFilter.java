package com.pethome.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域过滤器 —— 开发阶段允许所有来源
 */
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // 允许所有来源
        String origin = req.getHeader("Origin");
        if (origin != null) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            resp.setHeader("Access-Control-Allow-Origin", "*");
        }
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Max-Age", "3600");

        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
