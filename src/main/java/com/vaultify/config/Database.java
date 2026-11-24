package com.vaultify.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class Database implements AutoCloseable {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
            // The container may supply the driver on its own classpath instead.
        }
    }

    private final HikariDataSource dataSource;

    public Database(AppConfig config) {
        HikariConfig hikari = new HikariConfig();
        hikari.setJdbcUrl(config.dbUrl());
        if (config.dbDriver() != null) {
            hikari.setDriverClassName(config.dbDriver());
        }
        hikari.setUsername(config.dbUser());
        hikari.setPassword(config.dbPassword());
        hikari.setPoolName("vaultify-pool");
        hikari.setMaximumPoolSize(10);
        hikari.setMinimumIdle(2);
        hikari.setConnectionTimeout(10_000);
        this.dataSource = new HikariDataSource(hikari);
    }

    public Connection connection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() {
        dataSource.close();
    }
}
