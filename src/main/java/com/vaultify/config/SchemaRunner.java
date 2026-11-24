package com.vaultify.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Applies the idempotent schema. Statements are separated by a semicolon at the
 * end of a line, so the file never has to be split inside a statement.
 */
public final class SchemaRunner {

    private final Database database;

    public SchemaRunner(Database database) {
        this.database = database;
    }

    public void run() {
        List<String> statements = statements(read());
        try (Connection connection = database.connection();
             Statement statement = connection.createStatement()) {
            for (String ddl : statements) {
                statement.execute(ddl);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not apply schema.sql", e);
        }
    }

    private static List<String> statements(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String line : sql.split("\n")) {
            String trimmed = line.strip();
            if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                continue;
            }
            current.append(line).append('\n');
            if (trimmed.endsWith(";")) {
                String ddl = current.toString().strip();
                statements.add(ddl.substring(0, ddl.length() - 1).strip());
                current.setLength(0);
            }
        }
        if (!current.toString().isBlank()) {
            statements.add(current.toString().strip());
        }
        return statements;
    }

    private static String read() {
        try (InputStream input = SchemaRunner.class.getResourceAsStream("/schema.sql")) {
            if (input == null) {
                throw new IllegalStateException("schema.sql is missing from the classpath");
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read schema.sql", e);
        }
    }
}
