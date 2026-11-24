<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Create account"/>
<%@ include file="/WEB-INF/views/layouts/guest-top.jsp" %>

        <div class="mb-8">
            <h1 class="text-2xl font-semibold">Create your Vaultify account</h1>
            <p class="mt-2 text-sm text-stone-600">Start building a safer digital space.</p>
        </div>

        <%@ include file="/WEB-INF/views/partials/form-errors.jsp" %>

        <form method="post" action="${ctx}/register" class="space-y-5" novalidate>
            <input type="hidden" name="_token" value="${csrfToken}">

            <div>
                <label for="name" class="mb-2 block text-sm font-medium">Name</label>
                <input id="name" name="name" type="text" autocomplete="name" maxlength="255" required value="<c:out value='${old.name}'/>" class="w-full rounded-lg border border-stone-300 px-3 py-2.5 outline-none transition focus:border-violet-500 focus:ring-2 focus:ring-violet-100">
            </div>
            <div>
                <label for="email" class="mb-2 block text-sm font-medium">Email</label>
                <input id="email" name="email" type="email" autocomplete="email" maxlength="255" required value="<c:out value='${old.email}'/>" class="w-full rounded-lg border border-stone-300 px-3 py-2.5 outline-none transition focus:border-violet-500 focus:ring-2 focus:ring-violet-100">
            </div>
            <div>
                <label for="password" class="mb-2 block text-sm font-medium">Password</label>
                <input id="password" name="password" type="password" autocomplete="new-password" required class="w-full rounded-lg border border-stone-300 px-3 py-2.5 outline-none transition focus:border-violet-500 focus:ring-2 focus:ring-violet-100">
            </div>
            <div>
                <label for="password_confirmation" class="mb-2 block text-sm font-medium">Confirm password</label>
                <input id="password_confirmation" name="password_confirmation" type="password" autocomplete="new-password" required class="w-full rounded-lg border border-stone-300 px-3 py-2.5 outline-none transition focus:border-violet-500 focus:ring-2 focus:ring-violet-100">
            </div>

            <button type="submit" class="w-full rounded-lg bg-violet-700 px-4 py-3 font-medium text-white transition hover:bg-violet-800">Create account</button>
        </form>

        <p class="mt-6 text-center text-sm text-stone-600">Already have an account?
            <a href="${ctx}/login" class="font-medium text-violet-700 hover:text-violet-800">Sign in</a>
        </p>

<%@ include file="/WEB-INF/views/layouts/guest-bottom.jsp" %>
