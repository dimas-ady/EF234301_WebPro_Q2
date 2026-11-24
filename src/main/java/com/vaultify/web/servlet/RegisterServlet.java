package com.vaultify.web.servlet;

import com.vaultify.dao.DataAccessException;
import com.vaultify.model.User;
import com.vaultify.web.BaseServlet;
import com.vaultify.web.Validation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "register", urlPatterns = {"/register"})
public final class RegisterServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("pageTitle", "Create account");
        request.setAttribute("authTagline", "Start building a safer digital space.");
        render(request, response, "auth/register");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = trim(request.getParameter("name"));
        String email = trim(request.getParameter("email"));
        String password = request.getParameter("password");
        String confirmation = request.getParameter("password_confirmation");

        Map<String, String> old = new LinkedHashMap<>();
        old.put("name", name == null ? "" : name);
        old.put("email", email == null ? "" : email);

        List<String> errors = new ArrayList<>();

        if (Validation.isBlank(name)) {
            errors.add("Name is required.");
        } else if (name.length() > 255) {
            errors.add("Name may not exceed 255 characters.");
        }

        if (Validation.isBlank(email)) {
            errors.add("Email is required.");
        } else if (!Validation.isEmail(email)) {
            errors.add("Enter a valid email address.");
        } else if (email.length() > 255) {
            errors.add("Email may not exceed 255 characters.");
        } else if (app().users().existsByEmail(email)) {
            errors.add("This email is already in use.");
        }

        if (Validation.isBlank(password)) {
            errors.add("Password is required.");
        } else if (confirmation == null || !confirmation.equals(password)) {
            errors.add("Password confirmation does not match.");
        } else if (password.length() < 8) {
            errors.add("Password must be at least 8 characters.");
        }

        if (!errors.isEmpty()) {
            flashErrors(request, errors, old);
            redirect(request, response, "/register");
            return;
        }

        User user;
        try {
            user = app().users().insert(name, email, app().hasher().hash(password));
        } catch (DataAccessException e) {
            flashErrors(request, List.of("This email is already in use."), old);
            redirect(request, response, "/register");
            return;
        }

        HttpSession previous = request.getSession(false);
        if (previous != null) {
            previous.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        flashStatus(request, "Registration successful. Welcome to Vaultify.");
        redirect(request, response, "/vault");
    }
}
