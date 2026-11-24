package com.vaultify;

import com.vaultify.config.Database;
import com.vaultify.dao.CredentialDao;
import com.vaultify.dao.UserDao;
import com.vaultify.dao.WordlistDao;
import com.vaultify.security.EncryptionService;
import com.vaultify.security.PasswordHasher;
import com.vaultify.security.RateLimiter;
import com.vaultify.service.WordlistService;
import jakarta.servlet.ServletContext;

public final class AppContext {

    public static final String ATTRIBUTE = "vaultify.app";

    private final Database database;
    private final UserDao users;
    private final CredentialDao credentials;
    private final WordlistDao wordlist;
    private final WordlistService wordlistService;
    private final EncryptionService encryption;
    private final PasswordHasher hasher;
    private final RateLimiter rateLimiter;

    public AppContext(Database database, String appKey) {
        this.database = database;
        this.users = new UserDao(database);
        this.credentials = new CredentialDao(database);
        this.wordlist = new WordlistDao(database);
        this.encryption = new EncryptionService(appKey);
        this.hasher = new PasswordHasher();
        this.rateLimiter = new RateLimiter();
        this.wordlistService = new WordlistService(wordlist, credentials, encryption);
    }

    public static AppContext of(ServletContext context) {
        return (AppContext) context.getAttribute(ATTRIBUTE);
    }

    public Database database() {
        return database;
    }

    public UserDao users() {
        return users;
    }

    public CredentialDao credentials() {
        return credentials;
    }

    public WordlistDao wordlist() {
        return wordlist;
    }

    public WordlistService wordlistService() {
        return wordlistService;
    }

    public EncryptionService encryption() {
        return encryption;
    }

    public PasswordHasher hasher() {
        return hasher;
    }

    public RateLimiter rateLimiter() {
        return rateLimiter;
    }
}
