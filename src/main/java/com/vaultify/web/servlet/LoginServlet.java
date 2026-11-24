package com.vaultify.web.servlet;

import com.vaultify.model.User;
import com.vaultify.security.RememberTokens;
import com.vaultify.web.BaseServlet;
import com.vaultify.web.Validation;
import com.vaultify.web.filter.AuthFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "login", urlPatterns = {"/login"})
public final class LoginServlet extends BaseServlet {

    private static final int MAX_ATTEMPTS = 5;
    private static final int REMEMBER_SECONDS = 60 * 60 * 24 * 30;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("pageTitle", "Sign in");
        request.setAttribute("authTagline", "Manage your digital security with confidence.");
        render(request, response, "auth/login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = trim(request.getParameter("email"));
        String password = request.getParameter("password");

        Map<String, String> old = new LinkedHashMap<>();
        old.put("email", email == null ? "" : email);

        List<String> errors = new ArrayList<>();
        if (Validation.isBlank(email)) {
            errors.add("Email is required.");
        } else if (!Validation.isEmail(email)) {
            errors.add("Enter a valid email address.");
        }
        if (Validation.isBlank(password)) {
            errors.add("Password is required.");
        }

        String key = rateKey(email, request);
        if (errors.isEmpty() && app().rateLimiter().tooManyAttempts(key, MAX_ATTEMPTS)) {
            errors.add("Too many login attempts. Please try again later.");
        }

        if (!errors.isEmpty()) {
            flashErrors(request, errors, old);
            redirect(request, response, "/login");
            return;
        }

        Optional<User> found = app().users().findByEmail(email);
        boolean valid = found.isPresent() && app().hasher().verify(password, found.get().getPassword());
        if (!valid) {
            app().rateLimiter().hit(key);
            flashErrors(request, List.of("The provided email or password is incorrect."), old);
            redirect(request, response, "/login");
            return;
        }

        app().rateLimiter().clear(key);
        User user = found.get();

        HttpSession previous = request.getSession(false);
        String intended = previous == null ? null : (String) previous.getAttribute("url.intended");
        if (previous != null) {
            previous.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);

        if (request.getParameter("remember") != null) {
            remember(request, response, user);
        }

        if (intended != null && intended.startsWith("/") && !intended.startsWith("//")) {
            response.sendRedirect(intended);
            return;
        }
        redirect(request, response, "/vault");
    }

    private void remember(HttpServletRequest request, HttpServletResponse response, User user) {
        String token = RememberTokens.newToken();
        app().users().updateRememberToken(user.getId(), RememberTokens.hash(token));

        Cookie cookie = new Cookie(AuthFilter.REMEMBER_COOKIE, user.getId() + ":" + token);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        cookie.setMaxAge(REMEMBER_SECONDS);
        response.addCookie(cookie);
    }

    private static String rateKey(String email, HttpServletRequest request) {
        String address = request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
        String normalised = email == null ? "" : email.toLowerCase(Locale.ROOT);
        return normalised + "|" + address;
    }
}
