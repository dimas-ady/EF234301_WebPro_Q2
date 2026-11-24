package com.vaultify.web.servlet;

import com.vaultify.model.Credential;
import com.vaultify.model.User;
import com.vaultify.security.CryptoException;
import com.vaultify.web.BaseServlet;
import com.vaultify.web.Json;
import com.vaultify.web.Validation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "credential", urlPatterns = {"/vault/credentials", "/vault/credentials/*"})
public final class CredentialServlet extends BaseServlet {

    private static final Pattern EDIT = Pattern.compile("^/(\\d+)/edit$");
    private static final Pattern ITEM = Pattern.compile("^/(\\d+)$");
    private static final Pattern FAVORITE = Pattern.compile("^/(\\d+)/favorite$");
    private static final Pattern PASSWORD = Pattern.compile("^/(\\d+)/password$");

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(request.getMethod())) {
            doPatch(request, response);
            return;
        }
        super.service(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = path(request);
        if (path.isEmpty()) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        if ("/create".equals(path)) {
            request.setAttribute("isEdit", Boolean.FALSE);
            request.setAttribute("pageTitle", "Add credential");
            request.setAttribute("activeNav", "vault");
            render(request, response, "credentials/create");
            return;
        }

        Matcher edit = EDIT.matcher(path);
        if (edit.matches()) {
            Credential credential = requireOwned(request, response, Long.parseLong(edit.group(1)));
            if (credential == null) {
                return;
            }
            request.setAttribute("isEdit", Boolean.TRUE);
            request.setAttribute("credential", credential);
            request.setAttribute("notes", decryptNotes(credential));
            request.setAttribute("pageTitle", "Edit credential");
            request.setAttribute("activeNav", "vault");
            render(request, response, "credentials/edit");
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = path(request);
        if (path.isEmpty()) {
            store(request, response);
            return;
        }

        Matcher favorite = FAVORITE.matcher(path);
        if (favorite.matches()) {
            toggleFavorite(request, response, Long.parseLong(favorite.group(1)));
            return;
        }

        Matcher password = PASSWORD.matcher(path);
        if (password.matches()) {
            revealPassword(request, response, Long.parseLong(password.group(1)));
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Matcher item = ITEM.matcher(path(request));
        if (!item.matches()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        update(request, response, Long.parseLong(item.group(1)));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Matcher item = ITEM.matcher(path(request));
        if (!item.matches()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        destroy(request, response, Long.parseLong(item.group(1)));
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Matcher favorite = FAVORITE.matcher(path(request));
        if (!favorite.matches()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        toggleFavorite(request, response, Long.parseLong(favorite.group(1)));
    }

    private void store(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Form form = readForm(request, true);
        if (!form.errors.isEmpty()) {
            flashErrors(request, form.errors, form.old);
            redirect(request, response, "/vault/credentials/create");
            return;
        }

        Credential credential = new Credential();
        credential.setUserId(currentUser(request).getId());
        credential.setTitle(form.title);
        credential.setWebsiteUrl(form.websiteUrl);
        credential.setUsername(form.username);
        credential.setPasswordEncrypted(app().encryption().encrypt(form.password));
        credential.setNotesEncrypted(form.notes == null ? null : app().encryption().encrypt(form.notes));
        credential.setFavorite(false);

        Boolean match = app().wordlistService().check(form.password);
        credential.setWordlistMatch(match);
        credential.setWordlistCheckedAt(match == null ? null : Instant.now());

        app().credentials().insert(credential);
        flashStatus(request, "Credential added successfully.");
        redirect(request, response, "/vault");
    }

    private void update(HttpServletRequest request, HttpServletResponse response, long id) throws IOException {
        Credential credential = requireOwned(request, response, id);
        if (credential == null) {
            return;
        }

        Form form = readForm(request, false);
        if (!form.errors.isEmpty()) {
            flashErrors(request, form.errors, form.old);
            redirect(request, response, "/vault/credentials/" + id + "/edit");
            return;
        }

        credential.setTitle(form.title);
        credential.setWebsiteUrl(form.websiteUrl);
        credential.setUsername(form.username);
        credential.setNotesEncrypted(form.notes == null ? null : app().encryption().encrypt(form.notes));

        if (form.password != null) {
            credential.setPasswordEncrypted(app().encryption().encrypt(form.password));
            Boolean match = app().wordlistService().check(form.password);
            credential.setWordlistMatch(match);
            credential.setWordlistCheckedAt(match == null ? null : Instant.now());
        }

        app().credentials().update(credential);
        flashStatus(request, "Credential updated successfully.");
        redirect(request, response, "/vault");
    }

    private void destroy(HttpServletRequest request, HttpServletResponse response, long id) throws IOException {
        Credential credential = requireOwned(request, response, id);
        if (credential == null) {
            return;
        }
        app().credentials().delete(credential.getUserId(), credential.getId());
        flashStatus(request, "Credential deleted successfully.");
        redirect(request, response, "/vault");
    }

    private void toggleFavorite(HttpServletRequest request, HttpServletResponse response, long id) throws IOException {
        Credential credential = requireOwned(request, response, id);
        if (credential == null) {
            return;
        }
        boolean favorite = !credential.isFavorite();
        app().credentials().setFavorite(credential.getUserId(), credential.getId(), favorite);

        String message = favorite
                ? "Credential added to favorites."
                : "Credential removed from favorites.";
        Json.write(response, HttpServletResponse.SC_OK,
                "{\"is_favorite\":" + favorite + ",\"message\":\"" + Json.escape(message) + "\"}");
    }

    private void revealPassword(HttpServletRequest request, HttpServletResponse response, long id) throws IOException {
        Credential credential = requireOwned(request, response, id);
        if (credential == null) {
            return;
        }

        String password;
        try {
            password = app().encryption().decrypt(credential.getPasswordEncrypted());
        } catch (CryptoException e) {
            Json.write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "{\"message\":\"" + Json.escape("Could not decrypt this credential.") + "\"}");
            return;
        }

        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        Json.write(response, HttpServletResponse.SC_OK,
                "{\"password\":\"" + Json.escape(password == null ? "" : password) + "\"}");
    }

    private Credential requireOwned(HttpServletRequest request, HttpServletResponse response, long id)
            throws IOException {
        User user = currentUser(request);
        Optional<Credential> found = app().credentials().findOwned(user.getId(), id);
        if (found.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        return found.get();
    }

    private String decryptNotes(Credential credential) {
        if (credential.getNotesEncrypted() == null) {
            return null;
        }
        try {
            return app().encryption().decrypt(credential.getNotesEncrypted());
        } catch (CryptoException e) {
            return null;
        }
    }

    private static String path(HttpServletRequest request) {
        String path = request.getPathInfo();
        return path == null ? "" : path;
    }

    private static Form readForm(HttpServletRequest request, boolean passwordRequired) {
        Form form = new Form();
        form.title = emptyToNull(request.getParameter("title"));
        form.websiteUrl = emptyToNull(request.getParameter("website_url"));
        form.username = emptyToNull(request.getParameter("username"));
        form.password = emptyToNull(request.getParameter("password"));
        form.notes = emptyToNull(request.getParameter("notes"));

        if (form.title == null) {
            form.errors.add("Service name is required.");
        } else if (form.title.length() > 255) {
            form.errors.add("Service name may not exceed 255 characters.");
        }

        if (form.websiteUrl != null) {
            if (!Validation.isUrl(form.websiteUrl)) {
                form.errors.add("Enter a valid website URL.");
            } else if (form.websiteUrl.length() > 2048) {
                form.errors.add("Website URL is too long.");
            }
        }

        if (form.username != null && form.username.length() > 255) {
            form.errors.add("Username may not exceed 255 characters.");
        }

        if (passwordRequired && form.password == null) {
            form.errors.add("Password is required.");
        } else if (form.password != null && form.password.length() > 10000) {
            form.errors.add("Password is too long.");
        }

        if (form.notes != null && form.notes.length() > 10000) {
            form.errors.add("Notes are too long.");
        }

        form.old.put("title", form.title == null ? "" : form.title);
        form.old.put("website_url", form.websiteUrl == null ? "" : form.websiteUrl);
        form.old.put("username", form.username == null ? "" : form.username);
        form.old.put("notes", form.notes == null ? "" : form.notes);
        return form;
    }

    private static String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private static final class Form {
        private String title;
        private String websiteUrl;
        private String username;
        private String password;
        private String notes;
        private final List<String> errors = new ArrayList<>();
        private final Map<String, String> old = new LinkedHashMap<>();
    }
}
