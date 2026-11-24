<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="hasOld" value="${old != null}"/>
<c:set var="formTitle" value="${hasOld ? old.title : (isEdit ? credential.title : '')}"/>
<c:set var="formUrl" value="${hasOld ? old.website_url : (isEdit ? credential.websiteUrl : '')}"/>
<c:set var="formUsername" value="${hasOld ? old.username : (isEdit ? credential.username : '')}"/>
<c:set var="formNotes" value="${hasOld ? old.notes : (isEdit ? notes : '')}"/>

<c:choose>
    <c:when test="${isEdit}">
        <c:set var="formAction" value="${ctx}/vault/credentials/${credential.id}"/>
    </c:when>
    <c:otherwise>
        <c:set var="formAction" value="${ctx}/vault/credentials"/>
    </c:otherwise>
</c:choose>

<form method="post" action="${formAction}" class="mt-8 space-y-5" novalidate>
    <input type="hidden" name="_token" value="${csrfToken}">
    <c:if test="${isEdit}">
        <input type="hidden" name="_method" value="PUT">
    </c:if>

    <div>
        <label for="title" class="mb-2 block text-sm font-medium text-stone-800">Service name <span class="text-red-600">*</span></label>
        <input id="title" name="title" type="text" value="<c:out value='${formTitle}'/>" required maxlength="255" class="w-full rounded-xl border border-stone-200 px-4 py-3 text-sm outline-none focus:border-violet-500 focus:ring-2 focus:ring-violet-100">
    </div>

    <div>
        <label for="website_url" class="mb-2 block text-sm font-medium text-stone-800">Website URL</label>
        <input id="website_url" name="website_url" type="url" value="<c:out value='${formUrl}'/>" maxlength="2048" placeholder="https://example.com" class="w-full rounded-xl border border-stone-200 px-4 py-3 text-sm outline-none focus:border-violet-500 focus:ring-2 focus:ring-violet-100">
    </div>

    <div>
        <label for="username" class="mb-2 block text-sm font-medium text-stone-800">Username or email</label>
        <input id="username" name="username" type="text" value="<c:out value='${formUsername}'/>" maxlength="255" class="w-full rounded-xl border border-stone-200 px-4 py-3 text-sm outline-none focus:border-violet-500 focus:ring-2 focus:ring-violet-100">
    </div>

    <div>
        <label for="password" class="mb-2 block text-sm font-medium text-stone-800">Password <c:if test="${not isEdit}"><span class="text-red-600">*</span></c:if></label>
        <input id="password" name="password" type="password" ${isEdit ? '' : 'required'} maxlength="10000" autocomplete="${isEdit ? 'new-password' : 'current-password'}" class="w-full rounded-xl border border-stone-200 px-4 py-3 text-sm outline-none focus:border-violet-500 focus:ring-2 focus:ring-violet-100">
        <c:if test="${isEdit}">
            <p class="mt-2 text-xs text-stone-500">Leave blank to keep the current password.</p>
        </c:if>
    </div>

    <div>
        <label for="notes" class="mb-2 block text-sm font-medium text-stone-800">Notes</label>
        <textarea id="notes" name="notes" rows="5" maxlength="10000" class="w-full rounded-xl border border-stone-200 px-4 py-3 text-sm outline-none focus:border-violet-500 focus:ring-2 focus:ring-violet-100"><c:out value="${formNotes}"/></textarea>
    </div>

    <div class="flex flex-col-reverse gap-3 pt-3 sm:flex-row sm:justify-end">
        <a href="${ctx}/vault" class="rounded-xl border border-stone-200 px-4 py-3 text-center text-sm font-semibold text-stone-600 hover:bg-stone-50">Cancel</a>
        <button type="submit" class="rounded-xl bg-violet-700 px-4 py-3 text-sm font-semibold text-white hover:bg-violet-800">${isEdit ? 'Save changes' : 'Save credential'}</button>
    </div>
</form>
