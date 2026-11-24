package com.vaultify.dao;

import com.vaultify.config.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public final class WordlistDao {

    private final Database database;

    public WordlistDao(Database database) {
        this.database = database;
    }

    public boolean isAvailable() {
        String sql = "SELECT 1 FROM password_wordlist LIMIT 1";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            return rows.next();
        } catch (SQLException e) {
            throw new DataAccessException("Could not check wordlist availability", e);
        }
    }

    /**
     * The column uses a binary collation, so this comparison is exact and case sensitive.
     */
    public boolean matches(String password) {
        String sql = "SELECT 1 FROM password_wordlist WHERE password_value = ? LIMIT 1";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, password);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not match password", e);
        }
    }

    public int insertIgnore(List<String> values, String sourceName) {
        if (values.isEmpty()) {
            return 0;
        }
        String sql = "INSERT IGNORE INTO password_wordlist (password_value, source_name) VALUES (?, ?)";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String value : values) {
                statement.setString(1, value);
                statement.setString(2, sourceName);
                statement.addBatch();
            }
            int inserted = 0;
            for (int count : statement.executeBatch()) {
                if (count > 0 || count == Statement.SUCCESS_NO_INFO) {
                    inserted++;
                }
            }
            return inserted;
        } catch (SQLException e) {
            throw new DataAccessException("Could not insert wordlist entries", e);
        }
    }
}
