package com.vaultify.config;

public final class AppConfig {

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private final String dbDriver;
    private final String appKey;

    public AppConfig() {
        String explicitUrl = env("VAULTIFY_DB_URL", null);
        if (explicitUrl != null) {
            this.dbUrl = explicitUrl;
        } else {
            this.dbUrl = "jdbc:mysql://" + env("DB_HOST", "localhost")
                    + ":" + env("DB_PORT", "3306")
                    + "/" + env("DB_DATABASE", "vaultify")
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        }
        this.dbUser = env("DB_USERNAME", "vaultify");
        this.dbPassword = env("DB_PASSWORD", "secret");
        this.dbDriver = env("VAULTIFY_DB_DRIVER", null);
        this.appKey = env("VAULTIFY_APP_KEY", env("APP_KEY", null));
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            value = System.getProperty(name);
        }
        return value == null || value.isBlank() ? fallback : value;
    }

    public String dbUrl() {
        return dbUrl;
    }

    public String dbUser() {
        return dbUser;
    }

    public String dbPassword() {
        return dbPassword;
    }

    public String dbDriver() {
        return dbDriver;
    }

    public String appKey() {
        return appKey;
    }
}
