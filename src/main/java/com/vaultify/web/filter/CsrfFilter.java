package com.vaultify.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Set;

public final class CsrfFilter implements Filter {

    private static final String SESSION_KEY = "csrfToken";
    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(true);
        String token = (String) session.getAttribute(SESSION_KEY);
        if (token == null || token.isBlank()) {
            token = newToken();
            session.setAttribute(SESSION_KEY, token);
        }
        httpRequest.setAttribute("csrfToken", token);

        if (!SAFE_METHODS.contains(httpRequest.getMethod().toUpperCase(Locale.ROOT))) {
            String submitted = httpRequest.getHeader("X-CSRF-TOKEN");
            if (submitted == null) {
                submitted = httpRequest.getParameter("_token");
            }
            if (submitted == null || !matches(token, submitted)) {
                httpResponse.sendError(419, "CSRF token mismatch. Refresh the page and try again.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private static boolean matches(String expected, String actual) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    private static String newToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
