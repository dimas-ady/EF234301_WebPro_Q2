<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Edit Credential"/>
<c:set var="activeNav" value="vault"/>
<%@ include file="/WEB-INF/views/layouts/app-top.jsp" %>

    <div class="mx-auto max-w-3xl">
        <a href="${ctx}/vault" class="text-sm font-medium text-violet-700 hover:text-violet-800">&larr; Back to Vault</a>
        <div class="mt-5 rounded-2xl border border-stone-200 bg-white p-6 shadow-sm sm:p-8">
            <h2 class="text-2xl font-semibold tracking-tight">Edit credential</h2>
            <p class="mt-2 text-sm text-stone-600">Update access information without revealing the current password.</p>

            <%@ include file="/WEB-INF/views/partials/form-errors.jsp" %>
            <%@ include file="/WEB-INF/views/partials/credential-form.jsp" %>
        </div>
    </div>

<%@ include file="/WEB-INF/views/layouts/app-bottom.jsp" %>
