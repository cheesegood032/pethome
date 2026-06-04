package com.pethome.filter;

import jakarta.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {
    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String config = filterConfig.getInitParameter("encoding");
        if (config != null && !config.isEmpty()) {
            encoding = config;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
