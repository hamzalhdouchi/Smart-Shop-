package com.smartshop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartshop.enums.UserRole;
import com.smartshop.exception.ErrorResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuthFilter implements Filter {

    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        if (isPublicEndpoint(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            sendErrorResponse(httpResponse, 401, "User not authenticated. Please log in.", requestURI);
            return;
        }

        String userRole = (String) session.getAttribute("USER_ROLE");
        if (!requiresClientRole(requestURI, method) && UserRole.CLIENT.name().equals(userRole)) {
            sendErrorResponse(httpResponse, 403, "Access denied. Only administrators can perform this action.", requestURI);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/api/auth/login") ||
                uri.startsWith("/api/auth/register") ||
                uri.equals("/error");
    }


    private boolean requiresClientRole(String uri, String method) {
        if(uri.matches("/api/clients/[^/]+") && "GET".equals(method)) {
            return true;
        }else if(uri.matches("/api/clients/[^/]+/with-user") && "GET".equals(method)) {
            return true;
        }else {
            return false;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status,
                                   String message, String path) throws IOException {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(status == 401 ? "Unauthorized" : "Forbidden")
                .message(message)
                .path(path)
                .build();

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
