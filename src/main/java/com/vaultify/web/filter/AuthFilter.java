package com.vaultify.web.filter;

import com.vaultify.AppContext;
import com.vaultify.model.User;
import com.vaultify.security.RememberTokens;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

public final class AuthFilter implements Filter {

    public static final String REMEMBER_COOKIE = "vaultify_remember";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);

        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = fromRememberCookie(httpRequest, session);
        }

        if (user == null) {
            if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {
                String target = httpRequest.getRequestURI();
                if (httpRequest.getQueryString() != null) {
                    target = target + "?" + httpRequest.getQueryString();
                }
                session.setAttribute("url.intended", target);
            }
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    private static User fromRememberCookie(HttpServletRequest request, HttpSession session) {
        String value = readCookie(request, REMEMBER_COOKIE);
        if (value == null) {
            return null;
        }
        int separator = value.indexOf(':');
        if (separator <= 0) {
            return null;
        }
        try {
            long userId = Long.parseLong(value.substring(0, separator));
            String token = value.substring(separator + 1);
            if (token.isEmpty()) {
                return null;
            }
            AppContext app = AppContext.of(request.getServletContext());
            Optional<User> user = app.users()
                    .findByRememberToken(userId, RememberTokens.hash(token));
            if (user.isPresent()) {
                session.setAttribute("user", user.get());
                return user.get();
            }
        } catch (NumberFormatException ignored) {
            // Malformed cookie, treat as signed out.
        }
        return null;
    }

    private static String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
