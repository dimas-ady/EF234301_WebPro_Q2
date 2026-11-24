package com.vaultify.web.servlet;

import com.vaultify.model.Credential;
import com.vaultify.model.User;
import com.vaultify.web.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "vault", urlPatterns = {"/vault"})
public final class VaultServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        String filter = request.getParameter("filter");
        if (filter == null || filter.isBlank()) {
            filter = "all";
        }

        List<String> errors = new ArrayList<>();
        if (search != null && search.length() > 100) {
            errors.add("Search may not exceed 100 characters.");
        }
        if (!"all".equals(filter) && !"favorites".equals(filter)) {
            errors.add("Invalid filter.");
        }
        if (!errors.isEmpty()) {
            flashErrors(request, errors, Map.of());
            redirect(request, response, "/vault");
            return;
        }

        if (search != null && search.isBlank()) {
            search = null;
        }

        User user = currentUser(request);
        boolean favoritesOnly = "favorites".equals(filter);
        List<Credential> credentials = app().credentials().search(user.getId(), search, favoritesOnly);

        request.setAttribute("credentials", credentials);
        request.setAttribute("search", search);
        request.setAttribute("filter", filter);
        request.setAttribute("pageTitle", "Vault");
        request.setAttribute("activeNav", "vault");
        render(request, response, "vault");
    }
}
