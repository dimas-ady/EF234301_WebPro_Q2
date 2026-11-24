<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Method not allowed &middot; Vaultify</title>
    <link rel="stylesheet" href="${ctx}/assets/app.css">
</head>
<body class="guest">
<main class="error-shell">
    <p class="eyebrow">405</p>
    <h1>That request is not allowed</h1>
    <p class="error-copy">This address only accepts a form submission. Use the Log out button in the sidebar instead of opening the address directly.</p>
    <a class="button button-primary" href="${ctx}/vault">Back to the vault</a>
</main>
</body>
</html>
