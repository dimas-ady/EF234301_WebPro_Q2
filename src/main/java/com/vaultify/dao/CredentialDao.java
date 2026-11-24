package com.vaultify.dao;

import com.vaultify.config.Database;
import com.vaultify.model.Credential;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CredentialDao {

    private static final String COLUMNS =
            "id, user_id, title, website_url, username, password_encrypted, notes_encrypted, "
                    + "is_favorite, wordlist_match, wordlist_checked_at, created_at";

    private final Database database;

    public CredentialDao(Database database) {
        this.database = database;
    }

    public List<Credential> search(long userId, String search, boolean favoritesOnly) {
        StringBuilder sql = new StringBuilder("SELECT ").append(COLUMNS)
                .append(" FROM credentials WHERE user_id = ?");
        List<Object> parameters = new ArrayList<>();
        parameters.add(userId);

        if (favoritesOnly) {
            sql.append(" AND is_favorite = 1");
        }
        if (search != null && !search.isBlank()) {
            sql.append(" AND (title LIKE ? OR username LIKE ? OR website_url LIKE ?)");
            String like = "%" + search + "%";
            parameters.add(like);
            parameters.add(like);
            parameters.add(like);
        }
        sql.append(" ORDER BY created_at DESC, id DESC");

        List<Credential> credentials = new ArrayList<>();
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    credentials.add(map(rows));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not search credentials", e);
        }
        return credentials;
    }

    public Optional<Credential> findOwned(long userId, long id) {
        String sql = "SELECT " + COLUMNS + " FROM credentials WHERE id = ? AND user_id = ?";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, userId);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(map(rows)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load credential", e);
        }
    }

    public long insert(Credential credential) {
        String sql = "INSERT INTO credentials (user_id, title, website_url, username, password_encrypted, "
                + "notes_encrypted, is_favorite, wordlist_match, wordlist_checked_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, credential.getUserId());
            statement.setString(2, credential.getTitle());
            statement.setString(3, credential.getWebsiteUrl());
            statement.setString(4, credential.getUsername());
            statement.setString(5, credential.getPasswordEncrypted());
            statement.setString(6, credential.getNotesEncrypted());
            statement.setInt(7, credential.isFavorite() ? 1 : 0);
            setNullableBoolean(statement, 8, credential.getWordlistMatch());
            setNullableInstant(statement, 9, credential.getWordlistCheckedAt());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new DataAccessException("Insert did not return a generated id", null);
        } catch (SQLException e) {
            throw new DataAccessException("Could not create credential", e);
        }
    }

    public void update(Credential credential) {
        String sql = "UPDATE credentials SET title = ?, website_url = ?, username = ?, password_encrypted = ?, "
                + "notes_encrypted = ?, wordlist_match = ?, wordlist_checked_at = ? "
                + "WHERE id = ? AND user_id = ?";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, credential.getTitle());
            statement.setString(2, credential.getWebsiteUrl());
            statement.setString(3, credential.getUsername());
            statement.setString(4, credential.getPasswordEncrypted());
            statement.setString(5, credential.getNotesEncrypted());
            setNullableBoolean(statement, 6, credential.getWordlistMatch());
            setNullableInstant(statement, 7, credential.getWordlistCheckedAt());
            statement.setLong(8, credential.getId());
            statement.setLong(9, credential.getUserId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update credential", e);
        }
    }

    public void setFavorite(long userId, long id, boolean favorite) {
        String sql = "UPDATE credentials SET is_favorite = ? WHERE id = ? AND user_id = ?";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, favorite ? 1 : 0);
            statement.setLong(2, id);
            statement.setLong(3, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update favorite", e);
        }
    }

    public void delete(long userId, long id) {
        String sql = "DELETE FROM credentials WHERE id = ? AND user_id = ?";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete credential", e);
        }
    }

    public List<Credential> findAllForRecheck() {
        String sql = "SELECT id, password_encrypted FROM credentials ORDER BY id";
        List<Credential> credentials = new ArrayList<>();
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                Credential credential = new Credential();
                credential.setId(rows.getLong("id"));
                credential.setPasswordEncrypted(rows.getString("password_encrypted"));
                credentials.add(credential);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not list credentials for recheck", e);
        }
        return credentials;
    }

    public void updateWordlistStatus(long id, Boolean match, Instant checkedAt) {
        String sql = "UPDATE credentials SET wordlist_match = ?, wordlist_checked_at = ? WHERE id = ?";
        try (Connection connection = database.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setNullableBoolean(statement, 1, match);
            setNullableInstant(statement, 2, checkedAt);
            statement.setLong(3, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update wordlist status", e);
        }
    }

    private static void setNullableBoolean(PreparedStatement statement, int index, Boolean value)
            throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BOOLEAN);
        } else {
            statement.setBoolean(index, value);
        }
    }

    private static void setNullableInstant(PreparedStatement statement, int index, Instant value)
            throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(index, Timestamp.from(value));
        }
    }

    private static Credential map(ResultSet row) throws SQLException {
        Credential credential = new Credential();
        credential.setId(row.getLong("id"));
        credential.setUserId(row.getLong("user_id"));
        credential.setTitle(row.getString("title"));
        credential.setWebsiteUrl(row.getString("website_url"));
        credential.setUsername(row.getString("username"));
        credential.setPasswordEncrypted(row.getString("password_encrypted"));
        credential.setNotesEncrypted(row.getString("notes_encrypted"));
        credential.setFavorite(row.getBoolean("is_favorite"));

        int match = row.getInt("wordlist_match");
        credential.setWordlistMatch(row.wasNull() ? null : match != 0);

        Timestamp checkedAt = row.getTimestamp("wordlist_checked_at");
        credential.setWordlistCheckedAt(checkedAt == null ? null : checkedAt.toInstant());

        Timestamp createdAt = row.getTimestamp("created_at");
        credential.setCreatedAt(createdAt == null ? null : createdAt.toInstant());
        return credential;
    }
}
