<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="currentUser" value="${sessionScope.user}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="${csrfToken}">
    <title><c:out value="${empty pageTitle ? 'Vaultify' : pageTitle}"/> &middot; Vaultify</title>
    <link rel="stylesheet" href="${ctx}/assets/app.css?v=20261124">
    <link rel="stylesheet" href="${ctx}/assets/mobile-nav.css?v=20261125">
</head>
<body class="min-h-screen bg-stone-50 text-stone-900 antialiased">
<div id="app-shell" class="min-h-screen">
    <div id="sidebar-backdrop" class="fixed inset-0 z-30 hidden bg-stone-950/30 lg:hidden" data-sidebar-backdrop></div>
    <aside id="app-sidebar" class="fixed inset-y-0 left-0 z-40 flex w-72 -translate-x-full flex-col border-r border-stone-200 bg-white transition-transform duration-200 lg:translate-x-0" data-sidebar>
        <div class="flex h-20 items-center justify-between border-b border-stone-200 px-6">
            <a href="${ctx}/vault" class="flex items-center gap-3 text-lg font-semibold tracking-tight text-stone-900">
                <span class="flex h-9 w-9 items-center justify-center rounded-xl bg-violet-700 text-sm font-bold text-white">V</span>
                Vaultify
            </a>
            <button type="button" class="rounded-lg p-2 text-stone-500 hover:bg-stone-100 lg:hidden" aria-label="Close navigation" data-sidebar-close>
                <span aria-hidden="true" class="text-xl leading-none">&times;</span>
            </button>
        </div>
        <div class="flex-1 px-4 py-6">
            <p class="px-3 text-xs font-semibold uppercase tracking-wider text-stone-400">Main menu</p>
            <nav class="mt-3 space-y-1" aria-label="Main navigation">
                <a href="${ctx}/vault" class="flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-medium transition focus:outline-none focus:ring-2 focus:ring-violet-500 ${activeNav eq 'vault' ? 'bg-violet-50 text-violet-800' : 'text-stone-600 hover:bg-stone-100 hover:text-stone-900'}" ${activeNav eq 'vault' ? 'aria-current="page"' : ''}>
                    <span class="flex h-8 w-8 items-center justify-center rounded-lg bg-white text-violet-700 shadow-sm" aria-hidden="true">&#9643;</span>
                    Vault
                </a>
                <a href="${ctx}/password-generator" class="flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-medium transition focus:outline-none focus:ring-2 focus:ring-violet-500 ${activeNav eq 'generator' ? 'bg-violet-50 text-violet-800' : 'text-stone-600 hover:bg-stone-100 hover:text-stone-900'}" ${activeNav eq 'generator' ? 'aria-current="page"' : ''}>
                    <span class="flex h-8 w-8 items-center justify-center rounded-lg bg-white text-violet-700 shadow-sm" aria-hidden="true">&#9670;</span>
                    Password Generator
                </a>
            </nav>
        </div>
        <div class="border-t border-stone-200 p-4">
            <div class="mb-3 flex items-center gap-3 rounded-xl bg-stone-50 px-3 py-3">
                <span class="flex h-9 w-9 items-center justify-center rounded-full bg-violet-100 text-sm font-semibold text-violet-800"><c:out value="${currentUser.initial}"/></span>
                <div class="min-w-0">
                    <p class="truncate text-sm font-medium text-stone-800"><c:out value="${currentUser.name}"/></p>
                    <p class="truncate text-xs text-stone-500"><c:out value="${currentUser.email}"/></p>
                </div>
            </div>
            <form method="post" action="${ctx}/logout">
                <input type="hidden" name="_token" value="${csrfToken}">
                <button type="submit" class="flex min-h-11 w-full items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium text-stone-600 transition focus:outline-none focus:ring-2 focus:ring-violet-500 hover:bg-stone-100 hover:text-stone-900">
                    <span aria-hidden="true">&#8594;</span>
                    Log out
                </button>
            </form>
        </div>
    </aside>
    <div class="lg:pl-72">
        <header class="sticky top-0 z-20 flex h-20 items-center justify-between border-b border-stone-200 bg-white/95 px-4 backdrop-blur sm:px-6 lg:px-10">
            <div class="flex items-center gap-3">
                <button type="button" class="rounded-xl border border-stone-200 p-2.5 text-stone-600 hover:bg-stone-50 lg:hidden" aria-label="Open navigation" data-sidebar-toggle>
                    <span aria-hidden="true" class="text-lg">&#9776;</span>
                </button>
                <div>
                    <p class="text-xs font-medium uppercase tracking-wider text-violet-700">Your secure space</p>
                    <h1 class="text-lg font-semibold text-stone-900 sm:text-xl"><c:out value="${empty pageTitle ? 'Vault' : pageTitle}"/></h1>
                </div>
            </div>
        </header>
        <main class="min-h-[calc(100vh-5rem)] px-4 py-8 sm:px-6 lg:px-10 lg:py-10">
