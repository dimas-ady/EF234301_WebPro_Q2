package com.vaultify.model;

import java.time.Instant;

public final class Credential {

    private long id;
    private long userId;
    private String title;
    private String websiteUrl;
    private String username;
    private String passwordEncrypted;
    private String notesEncrypted;
    private boolean favorite;
    private Boolean wordlistMatch;
    private Instant wordlistCheckedAt;
    private Instant createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }

    public String getNotesEncrypted() {
        return notesEncrypted;
    }

    public void setNotesEncrypted(String notesEncrypted) {
        this.notesEncrypted = notesEncrypted;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Boolean getWordlistMatch() {
        return wordlistMatch;
    }

    public void setWordlistMatch(Boolean wordlistMatch) {
        this.wordlistMatch = wordlistMatch;
    }

    public Instant getWordlistCheckedAt() {
        return wordlistCheckedAt;
    }

    public void setWordlistCheckedAt(Instant wordlistCheckedAt) {
        this.wordlistCheckedAt = wordlistCheckedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
