<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Sign in"/>
<%@ include file="/WEB-INF/views/layouts/guest-top.jsp" %>

        <div class="mb-8">
            <h1 class="text-2xl font-semibold">Sign in to Vaultify</h1>
            <p class="mt-2 text-sm text-stone-600">Manage your digital security with confidence.</p>
        </div>

        <%@ include file="/WEB-INF/views/partials/form-errors.jsp" %>

        <form method="post" action="${ctx}/login" class="space-y-5" novalidate>
            <input type="hidden" name="_token" value="${csrfToken}">

            <div>
                <label for="email" class="mb-2 block text-sm font-medium">Email</label>
                <input id="email" name="email" type="email" autocomplete="email" required value="<c:out value='${old.email}'/>" class="w-full rounded-lg border border-stone-300 px-3 py-2.5 outline-none transition focus:border-violet-500 focus:ring-2 focus:ring-violet-100">
            </div>

            <div>
                <label for="password" class="mb-2 block text-sm font-medium">Password</label>
                <input id="password" name="password" type="password" autocomplete="current-password" required class="w-full rounded-lg border border-stone-300 px-3 py-2.5 outline-none transition focus:border-violet-500 focus:ring-2 focus:ring-violet-100">
            </div>

            <label class="flex items-center gap-2 text-sm text-stone-600">
                <input type="checkbox" name="remember" value="1" class="rounded border-stone-300 text-violet-600 focus:ring-violet-500">
                Remember me
            </label>

            <button type="submit" class="w-full rounded-lg bg-violet-700 px-4 py-3 font-medium text-white transition hover:bg-violet-800">Sign in</button>
        </form>

        <p class="mt-6 text-center text-sm text-stone-600">Don't have an account?
            <a href="${ctx}/register" class="font-medium text-violet-700 hover:text-violet-800">Register</a>
        </p>

<%@ include file="/WEB-INF/views/layouts/guest-bottom.jsp" %>
