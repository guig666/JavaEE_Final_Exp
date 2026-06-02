package com.konsonx.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        // 确定有效的 origin
        String effectiveOrigin;
        if (origin != null && !origin.isEmpty() && !"null".equals(origin)) {
            // 正常的 Origin
            effectiveOrigin = origin;
        } else if (referer != null && !referer.isEmpty()) {
            // 从 Referer 提取 origin
            try {
                java.net.URL url = new java.net.URL(referer);
                effectiveOrigin = url.getProtocol() + "://" + url.getHost();
                if (url.getPort() != -1) {
                    effectiveOrigin += ":" + url.getPort();
                }
            } catch (Exception e) {
                effectiveOrigin = "http://localhost:5174";
            }
        } else {
            // 默认使用前端端口
            effectiveOrigin = "http://localhost:5174";
        }

        // 设置 CORS 头
        response.setHeader("Access-Control-Allow-Origin", effectiveOrigin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With, Accept, Authorization, Cookie, X-Admin-Account");
        response.setHeader("Access-Control-Expose-Headers", "Set-Cookie");
        response.setHeader("Access-Control-Max-Age", "3600");

        // 调试日志
        System.out.println("[CORS] Origin: " + origin + ", Effective: " + effectiveOrigin + ", Method: " + request.getMethod() + ", URI: " + request.getRequestURI());

        // 处理 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        return true;
    }
}