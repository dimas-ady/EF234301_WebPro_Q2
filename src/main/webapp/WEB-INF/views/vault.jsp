
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Vault"/>
<c:set var="activeNav" value="vault"/>
<%@ include file="/WEB-INF/views/layouts/app-top.jsp" %>

        <div class="vault-page">
        <section class="vault-heading">
            <div>
                <p class="section-kicker">Your credential collection</p>
                <h2 class="section-title">Vault</h2>
                <p class="section-copy">Store and manage access to your digital services from one secure space.</p>
            </div>
            <a class="button button-primary" href="${ctx}/vault/credentials/create"><span aria-hidden="true">+</span> Add credential</a>
        </section>

        <section class="vault-toolbar" aria-label="Vault search and filters">
            <form class="search-form" method="get" action="${ctx}/vault" role="search">
                <label class="visually-hidden" for="search">Search credentials</label>
                <div class="search-input-wrap">
                    <span class="search-icon" aria-hidden="true">&#9906;</span>
                    <input id="search" name="search" type="search" maxlength="100"
                           placeholder="Search services, usernames, or URLs..."
                           value="<c:out value='${search}'/>">
                </div>
                <input type="hidden" name="filter" value="${filter}">
                <button type="submit" class="button search-submit">Search</button>
                <c:if test="${not empty search}">
                    <a class="button clear-search" href="${ctx}/vault?filter=${filter}">Clear search</a>
                </c:if>
            </form>
            <div class="filter-links">
                <a class="filter-link ${filter eq 'all' ? 'is-active' : ''}" href="${ctx}/vault?search=${search}&filter=all">All</a>
                <a class="filter-link ${filter eq 'favorites' ? 'is-active' : ''}" href="${ctx}/vault?search=${search}&filter=favorites">Favorites</a>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty credentials}">
                <div class="empty-state">
                    <div class="empty-icon" aria-hidden="true">&#9671;</div>
                    <c:choose>
                        <c:when test="${not empty search}">
                            <h2>No credentials found</h2>
                            <p>No credentials match "<c:out value="${search}"/>".</p>
                        </c:when>
                        <c:when test="${filter eq 'favorites'}">
                            <h2>No favorites yet</h2>
                            <p>Credentials you mark as favorites will appear here.</p>
                        </c:when>
                        <c:otherwise>
                            <h2>No credentials yet</h2>
                            <p>Credentials you save will appear here. Start by adding your first service login.</p>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${empty search and filter eq 'all'}">
                        <a class="button button-ghost" href="${ctx}/vault/credentials/create">Add your first credential</a>
                    </c:if>
                </div>
            </c:when>
            <c:otherwise>
                <p class="credential-note">This check only compares passwords with the available local wordlist. A result does not prove that an account has actually been compromised.</p>
                <div class="credential-grid">
                    <c:forEach var="credential" items="${credentials}">
                        <c:choose>
                            <c:when test="${credential.wordlistMatch == null}">
                                <c:set var="wordlistText" value="Not checked"/>
                                <c:set var="wordlistClass" value="is-neutral"/>
                            </c:when>
                            <c:when test="${credential.wordlistMatch}">
                                <c:set var="wordlistText" value="Found in local wordlist"/>
                                <c:set var="wordlistClass" value="is-danger"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="wordlistText" value="Not found in local wordlist"/>
                                <c:set var="wordlistClass" value="is-success"/>
                            </c:otherwise>
                        </c:choose>

                        <article class="credential-card" data-credential-card>
                            <header class="credential-head">
                                <div class="credential-heading">
                                    <h2 class="credential-title"><c:out value="${credential.title}"/></h2>
                                    <c:choose>
                                        <c:when test="${not empty credential.websiteUrl}">
                                            <a class="credential-site"
                                               href="<c:out value='${credential.websiteUrl}'/>"
                                               target="_blank" rel="noopener noreferrer"><c:out value="${credential.websiteUrl}"/></a>
                                        </c:when>
                                        <c:otherwise>
                                            <p class="credential-site is-muted">Not provided</p>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <button type="button"
                                        class="favorite-button ${credential.favorite ? 'is-favorite' : ''}"
                                        data-favorite-url="${ctx}/vault/credentials/${credential.id}/favorite"
                                        aria-pressed="${credential.favorite}"
                                        aria-label="${credential.favorite ? 'Remove from favorites' : 'Add to favorites'}">
                                    <span aria-hidden="true">${credential.favorite ? '★' : '☆'}</span>
                                    <span class="favorite-label">${credential.favorite ? 'Favorite' : 'Add favorite'}</span>
                                </button>
                            </header>

                            <dl class="credential-meta">
                                <div class="meta-row">
                                    <dt>Username</dt>
                                    <dd>
                                        <c:choose>
                                            <c:when test="${not empty credential.username}">
                                                <span><c:out value="${credential.username}"/></span>
                                                <button type="button" class="text-button"
                                                        data-copy-value="<c:out value='${credential.username}'/>">Copy</button>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="is-muted">Not provided</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </dd>
                                </div>

                                <div class="meta-row">
                                    <dt>Password</dt>
                                    <dd class="secret-row">
                                        <span class="secret" id="secret-${credential.id}" data-secret aria-label="Masked password">••••••••</span>
                                        <button type="button" class="text-button" data-password-action="reveal"
                                                data-password-url="${ctx}/vault/credentials/${credential.id}/password"
                                                data-password-target="secret-${credential.id}">Reveal</button>
                                        <button type="button" class="text-button" data-password-action="copy"
                                                data-password-url="${ctx}/vault/credentials/${credential.id}/password"
                                                data-password-target="secret-${credential.id}">Copy</button>
                                    </dd>
                                </div>
                            </dl>

                            <p class="wordlist-badge ${wordlistClass}">${wordlistText}</p>
                            <footer class="credential-actions">
                                <a class="button button-ghost" href="${ctx}/vault/credentials/${credential.id}/edit">Edit</a>
                                <form method="post" action="${ctx}/vault/credentials/${credential.id}"
                                      data-confirm="Delete this credential?">
                                    <input type="hidden" name="_token" value="${csrfToken}">
                                    <input type="hidden" name="_method" value="DELETE">
                                    <button type="submit" class="button button-danger">Delete</button>
                                </form>
                            </footer>
                        </article>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
        <c:if test="${not empty status}">
            <p class="vault-status" role="status"><c:out value="${status}"/></p>
        </c:if>
        </div>

<%@ include file="/WEB-INF/views/layouts/app-bottom.jsp" %>
