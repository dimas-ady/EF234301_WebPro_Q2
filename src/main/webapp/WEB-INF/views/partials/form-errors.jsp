<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${not empty errors}">
    <div class="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700" role="alert">
        <ul class="list-disc space-y-1 pl-5">
            <c:forEach var="error" items="${errors}">
                <li><c:out value="${error}"/></li>
            </c:forEach>
        </ul>
    </div>
</c:if>
