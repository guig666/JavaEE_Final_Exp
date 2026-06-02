package com.konsonx.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ManageLoginCheckInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 放行登录接口
        String uri = request.getRequestURI();
        if (uri.contains("/login") || uri.contains("/api/manage/administrators/account/") && uri.contains("/actions/login")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Object admin = (session != null) ? session.getAttribute("admin") : null;

        // 检查请求头中的管理员账号（用于跨域场景）
        if (admin == null) {
            String adminAccount = request.getHeader("X-Admin-Account");
            if (adminAccount != null && !adminAccount.isEmpty()) {
                // 请求头中有管理员账号，认为已登录
                if (session == null) {
                    session = request.getSession(true);
                }
                session.setAttribute("admin", adminAccount);
                return true;
            }
            
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":-1,\"msg\":\"请先登录\"}");
            return false;
        }
        return true;
    }
}