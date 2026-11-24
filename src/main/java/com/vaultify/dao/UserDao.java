package com.vaultify.dao;

import com.vaultify.config.Database;
import com.vaultify.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

public final class UserDao {

    private static final String COLUMNS =
            "id, name, email, password, remember_token, created_at";

    private final Database database;

    public UserDao(Database database) {
        this.database = database;
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE email = ?";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(map(rows)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not find user by email", e);
        }
    }

    public Optional<User> findById(long id) {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE id = ?";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(map(rows)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not find user by id", e);
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not check email", e);
        }
    }

    public User insert(String name, String email, String passwordHash) {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    User user = new User();
                    user.setId(keys.getLong(1));
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(passwordHash);
                    return user;
                }
            }
            throw new DataAccessException("Insert did not return a generated id", null);
        } catch (SQLException e) {
            throw new DataAccessException("Could not create user", e);
        }
    }

    public void updateRememberToken(long userId, String tokenHash) {
        String sql = "UPDATE users SET remember_token = ? WHERE id = ?";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tokenHash);
            statement.setLong(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update remember token", e);
        }
    }

    public Optional<User> findByRememberToken(long userId, String tokenHash) {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE id = ? AND remember_token = ?";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setString(2, tokenHash);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(map(rows)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not find user by remember token", e);
        }
    }

    private static User map(ResultSet row) throws SQLException {
        User user = new User();
        user.setId(row.getLong("id"));
        user.setName(row.getString("name"));
        user.setEmail(row.getString("email"));
        user.setPassword(row.getString("password"));
        user.setRememberToken(row.getString("remember_token"));
        Timestamp created = row.getTimestamp("created_at");
        user.setCreatedAt(created == null ? null : created.toInstant());
        return user;
    }
}
