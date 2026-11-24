<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Session expired &middot; Vaultify</title>
    <link rel="stylesheet" href="${ctx}/assets/app.css">
</head>
<body class="guest">
<main class="error-shell">
    <p class="eyebrow">419</p>
    <h1>Your session expired</h1>
    <p class="error-copy">The security token for that form was missing or out of date. Go back, refresh the page, and submit again.</p>
    <a class="button button-primary" href="${ctx}/vault">Back to the vault</a>
</main>
</body>
</html>
