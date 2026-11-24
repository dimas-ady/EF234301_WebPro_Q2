package com.vaultify.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * Supports form based PUT, PATCH and DELETE through a hidden {@code _method} field,
 * mirroring Laravel's method spoofing.
 */
public final class MethodOverrideFilter implements Filter {

    private static final Set<String> ALLOWED = Set.of("PUT", "PATCH", "DELETE");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            String override = httpRequest.getParameter("_method");
            if (override != null) {
                String method = override.toUpperCase(Locale.ROOT);
                if (ALLOWED.contains(method)) {
                    chain.doFilter(new MethodRequest(httpRequest, method), response);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    private static final class MethodRequest extends HttpServletRequestWrapper {

        private final String method;

        private MethodRequest(HttpServletRequest request, String method) {
            super(request);
            this.method = method;
        }

        @Override
        public String getMethod() {
            return method;
        }
    }
}
