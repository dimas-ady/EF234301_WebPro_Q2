package com.vaultify.web;

import com.vaultify.AppContext;
import com.vaultify.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class BaseServlet extends HttpServlet {

    protected AppContext app() {
        return AppContext.of(getServletContext());
    }

    protected User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    protected void render(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String location)
            throws IOException {
        response.sendRedirect(request.getContextPath() + location);
    }

    protected static String trim(String value) {
        return value == null ? null : value.strip();
    }

    protected void flashStatus(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute("flash.status", message);
    }

    protected void flashErrors(HttpServletRequest request, List<String> errors, Map<String, String> old) {
        HttpSession session = request.getSession(true);
        session.setAttribute("flash.errors", errors);
        session.setAttribute("flash.old", old);
    }
}
