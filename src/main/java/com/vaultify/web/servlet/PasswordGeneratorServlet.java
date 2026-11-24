package com.vaultify.web.servlet;

import com.vaultify.web.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "passwordGenerator", urlPatterns = {"/password-generator"})
public final class PasswordGeneratorServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("pageTitle", "Password Generator");
        request.setAttribute("activeNav", "generator");
        render(request, response, "password-generator");
    }
}
