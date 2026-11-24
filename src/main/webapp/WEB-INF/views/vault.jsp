<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Vault"/>
<c:set var="activeNav" value="vault"/>
<%@ include file="/WEB-INF/views/layouts/app-top.jsp" %>

        <div class="mx-auto max-w-7xl space-y-8">
            <section class="flex flex-col justify-between gap-5 sm:flex-row sm:items-end">
                <div>
                    <p class="text-sm font-medium text-violet-700">Your credential collection</p>
                    <h2 class="mt-2 text-3xl font-semibold tracking-tight text-stone-900">Vault</h2>
                    <p class="mt-3 max-w-2xl text-sm leading-6 text-stone-600">Store and manage access to your digital services from one secure space.</p>
                </div>
                <a href="${ctx}/vault/credentials/create"
                   class="inline-flex items-center justify-center gap-2 rounded-xl bg-violet-700 px-4 py-3 text-sm font-semibold text-white transition hover:bg-violet-800 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:ring-offset-2">
                    <span class="text-lg leading-none" aria-hidden="true">+</span>
                    Add credential
                </a>
            </section>

            <section class="space-y-4" aria-label="Vault search and filters">
                <form method="get" action="${ctx}/vault" class="flex max-w-2xl flex-col gap-3 sm:flex-row" role="search">
                    <label for="credential-search" class="sr-only">Search credentials</label>
                    <div class="relative flex-1">
                        <span class="pointer-events-none absolute inset-y-0 left-4 flex items-center text-stone-400" aria-hidden="true">&#9906;</span>
                        <input id="credential-search" name="search" type="search" maxlength="100"
                               placeholder="Search services, usernames, or URLs..."
                               value="<c:out value='${search}'/>"
                               class="w-full rounded-xl border border-stone-200 bg-white px-11 py-3 text-sm text-stone-900 outline-none transition placeholder:text-stone-400 focus:border-violet-500 focus:ring-2 focus:ring-violet-100">
                    </div>
                    <input type="hidden" name="filter" value="${filter}">
                    <button type="submit" class="rounded-xl bg-stone-900 px-5 py-3 text-sm font-medium text-white transition hover:bg-stone-800">Search</button>
                    <c:if test="${not empty search}">
                        <a href="${ctx}/vault?filter=${filter}"
                           class="rounded-xl border border-stone-200 bg-white px-5 py-3 text-center text-sm font-medium text-stone-600 transition hover:border-violet-300 hover:text-violet-700">Clear search</a>
                    </c:if>
                </form>
                <div class="flex flex-wrap gap-2">
                    <a href="${ctx}/vault?search=${search}&filter=all"
                       class="rounded-lg px-4 py-2 text-sm font-medium transition ${filter eq 'all' ? 'bg-stone-900 text-white' : 'border border-stone-200 bg-white text-stone-600 hover:border-violet-300 hover:text-violet-700'}">All</a>
                    <a href="${ctx}/vault?search=${search}&filter=favorites"
                       class="rounded-lg px-4 py-2 text-sm font-medium transition ${filter eq 'favorites' ? 'bg-stone-900 text-white' : 'border border-stone-200 bg-white text-stone-600 hover:border-violet-300 hover:text-violet-700'}">Favorites</a>
                </div>
            </section>

            <c:choose>
                <c:when test="${empty credentials}">
                    <section class="rounded-2xl border border-dashed border-stone-300 bg-white px-6 py-16 text-center sm:px-12" aria-labelledby="empty-vault-title">
                        <div class="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-violet-50 text-2xl text-violet-700" aria-hidden="true">&#9671;</div>
                        <c:choose>
                            <c:when test="${not empty search}">
                                <h3 id="empty-vault-title" class="mt-5 text-lg font-semibold text-stone-900">No credentials found</h3>
                                <p class="mx-auto mt-2 max-w-md text-sm leading-6 text-stone-500">No credentials match "<c:out value="${search}"/>".</p>
                            </c:when>
                            <c:when test="${filter eq 'favorites'}">
                                <h3 id="empty-vault-title" class="mt-5 text-lg font-semibold text-stone-900">No favorites yet</h3>
                                <p class="mx-auto mt-2 max-w-md text-sm leading-6 text-stone-500">Credentials you mark as favorites will appear here.</p>
                            </c:when>
                            <c:otherwise>
                                <h3 id="empty-vault-title" class="mt-5 text-lg font-semibold text-stone-900">No credentials yet</h3>
                                <p class="mx-auto mt-2 max-w-md text-sm leading-6 text-stone-500">Credentials you save will appear here. Start by adding your first service login.</p>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${empty search and filter eq 'all'}">
                            <a href="${ctx}/vault/credentials/create"
                               class="mt-6 inline-flex rounded-xl border border-violet-200 px-4 py-2.5 text-sm font-semibold text-violet-700 transition hover:bg-violet-50 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:ring-offset-2">Add your first credential</a>
                        </c:if>
                    </section>
                </c:when>
                <c:otherwise>
                    <p class="rounded-xl border border-stone-200 bg-stone-50 px-4 py-3 text-xs leading-5 text-stone-600">
                        This check only compares passwords with the available local wordlist. A result does not prove that an account has actually been compromised.
                    </p>
                    <section class="grid gap-4 md:grid-cols-2" aria-label="Credential list">
                        <c:forEach var="credential" items="${credentials}">
                            <c:choose>
                                <c:when test="${credential.wordlistMatch == null}">
                                    <c:set var="wordlistText" value="Not checked"/>
                                    <c:set var="wordlistColor" value="text-stone-500"/>
                                </c:when>
                                <c:when test="${credential.wordlistMatch}">
                                    <c:set var="wordlistText" value="Found in local wordlist"/>
                                    <c:set var="wordlistColor" value="text-amber-700"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="wordlistText" value="Not found in local wordlist"/>
                                    <c:set var="wordlistColor" value="text-emerald-700"/>
                                </c:otherwise>
                            </c:choose>

                            <article class="rounded-2xl border border-stone-200 bg-white p-5 shadow-sm" data-credential-card>
                                <div class="flex items-start justify-between gap-4">
                                    <div class="min-w-0">
                                        <h3 class="truncate text-lg font-semibold text-stone-900"><c:out value="${credential.title}"/></h3>
                                        <c:if test="${not empty credential.websiteUrl}">
                                            <a href="<c:out value='${credential.websiteUrl}'/>" target="_blank" rel="noopener noreferrer"
                                               class="mt-1 block truncate text-sm text-violet-700 hover:text-violet-800"><c:out value="${credential.websiteUrl}"/></a>
                                        </c:if>
                                    </div>
                                    <button type="button"
                                            data-favorite-url="${ctx}/vault/credentials/${credential.id}/favorite"
                                            aria-pressed="${credential.favorite}"
                                            aria-label="${credential.favorite ? 'Remove from favorites' : 'Add to favorites'}"
                                            class="rounded-lg px-2.5 py-1 text-xs font-medium transition ${credential.favorite ? 'bg-amber-50 text-amber-600' : 'bg-stone-50 text-stone-500 hover:text-amber-600'}">
                                        <span data-favorite-icon aria-hidden="true">${credential.favorite ? '★' : '☆'}</span>
                                        <span class="favorite-label" data-favorite-label>${credential.favorite ? 'Favorite' : 'Add favorite'}</span>
                                    </button>
                                </div>

                                <dl class="mt-5 space-y-3 text-sm">
                                    <div class="flex justify-between gap-4">
                                        <dt class="text-stone-500">Username</dt>
                                        <dd class="flex max-w-[65%] items-center gap-2 truncate text-right text-stone-800">
                                            <c:choose>
                                                <c:when test="${not empty credential.username}">
                                                    <span class="truncate"><c:out value="${credential.username}"/></span>
                                                    <button type="button" data-copy-value="<c:out value='${credential.username}'/>"
                                                            class="shrink-0 text-xs font-medium text-violet-700 hover:text-violet-800">Copy</button>
                                                </c:when>
                                                <c:otherwise><span class="truncate">Not provided</span></c:otherwise>
                                            </c:choose>
                                        </dd>
                                    </div>
                                    <div class="flex justify-between gap-4">
                                        <dt class="text-stone-500">Password</dt>
                                        <dd class="flex items-center gap-2">
                                            <span id="secret-${credential.id}" data-secret class="font-mono tracking-widest text-stone-700" aria-label="Masked password">••••••••</span>
                                            <button type="button" data-password-action="reveal"
                                                    data-password-url="${ctx}/vault/credentials/${credential.id}/password"
                                                    data-password-target="secret-${credential.id}"
                                                    class="text-xs font-medium text-violet-700 hover:text-violet-800">Reveal</button>
                                            <button type="button" data-password-action="copy"
                                                    data-password-url="${ctx}/vault/credentials/${credential.id}/password"
                                                    data-password-target="secret-${credential.id}"
                                                    class="text-xs font-medium text-violet-700 hover:text-violet-800">Copy</button>
                                        </dd>
                                    </div>
                                    <div class="flex justify-between gap-4">
                                        <dt class="text-stone-500">Local wordlist</dt>
                                        <dd class="text-right text-xs font-medium ${wordlistColor}"><c:out value="${wordlistText}"/></dd>
                                    </div>
                                </dl>
                                <p data-credential-message class="mt-3 min-h-4 text-xs text-stone-500" aria-live="polite"></p>
                                <div class="mt-2 flex items-center justify-end gap-2 border-t border-stone-100 pt-4">
                                    <a href="${ctx}/vault/credentials/${credential.id}/edit"
                                       class="rounded-lg border border-stone-200 px-3 py-2 text-sm font-medium text-stone-600 transition hover:border-violet-300 hover:text-violet-700">Edit</a>
                                    <form method="post" action="${ctx}/vault/credentials/${credential.id}" data-confirm="Delete this credential?">
                                        <input type="hidden" name="_token" value="${csrfToken}">
                                        <input type="hidden" name="_method" value="DELETE">
                                        <button type="submit" class="rounded-lg border border-red-200 px-3 py-2 text-sm font-medium text-red-600 transition hover:bg-red-50">Delete</button>
                                    </form>
                                </div>
                            </article>
                        </c:forEach>
                    </section>
                </c:otherwise>
            </c:choose>

            <c:if test="${not empty status}">
                <p class="rounded-xl border border-violet-200 bg-violet-50 px-4 py-3 text-sm text-violet-800" role="status"><c:out value="${status}"/></p>
            </c:if>
        </div>

<%@ include file="/WEB-INF/views/layouts/app-bottom.jsp" %>
