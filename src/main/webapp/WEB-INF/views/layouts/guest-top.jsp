<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="${csrfToken}">
    <title><c:out value="${empty pageTitle ? 'Vaultify' : pageTitle}"/> · Vaultify</title>
    <link rel="stylesheet" href="${ctx}/assets/app.css?v=20261124">
</head>
<body class="min-h-screen bg-stone-50 text-stone-900">
<main class="flex min-h-screen items-center justify-center px-4 py-12">
    <div class="w-full max-w-md">
        <a href="${ctx}/login" class="mb-8 block text-center text-2xl font-semibold tracking-tight text-violet-700">Vaultify</a>
        <section class="rounded-2xl border border-stone-200 bg-white p-8 shadow-sm">
