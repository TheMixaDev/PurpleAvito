package org.bigbrainmm.avitopricesapi.configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.bigbrainmm.avitopricesapi.StaticStorage;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class AvailableFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(!StaticStorage.isAvailable.get()) {
            System.exit(0);
            HttpServletResponse hsr = (HttpServletResponse) servletResponse;
            hsr.setStatus(503);
            hsr.setHeader("X-Powered-By", "Express");
            hsr.setHeader("Content-Type", "text/html; charset=utf-8");
            hsr.setHeader("Connection", "keep-alive");
            hsr.setHeader("Keep-Alive", "timeout=5");
            hsr.setHeader("Vary", null);
            hsr.setHeader("Cache-Control", null);
            hsr.setHeader("Pragma", null);
            hsr.setHeader("Expires", null);
            hsr.setHeader("X-Frame-Options", null);
            hsr.setHeader("Transfer-Encoding", null);
            hsr.setHeader("X-Content-Type-Options", null);
            hsr.setHeader("X-XSS-Protection", null);
            hsr.getWriter().write("1");
            hsr.getWriter().flush();
            hsr.getWriter().close();
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
