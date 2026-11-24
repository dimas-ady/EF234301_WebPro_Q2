package com.vaultify.service;

import com.vaultify.dao.CredentialDao;
import com.vaultify.dao.WordlistDao;
import com.vaultify.model.Credential;
import com.vaultify.security.CryptoException;
import com.vaultify.security.EncryptionService;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class WordlistService {

    public static final int MAX_ENTRY_LENGTH = 255;
    private static final int BATCH_SIZE = 500;

    private final WordlistDao wordlist;
    private final CredentialDao credentials;
    private final EncryptionService encryption;

    public WordlistService(WordlistDao wordlist, CredentialDao credentials, EncryptionService encryption) {
        this.wordlist = wordlist;
        this.credentials = credentials;
        this.encryption = encryption;
    }

    public boolean isAvailable() {
        return wordlist.isAvailable();
    }

    public boolean matches(String password) {
        return wordlist.matches(password);
    }

    public Boolean check(String password) {
        return isAvailable() ? matches(password) : null;
    }

    public ImportResult importFile(Path path) {
        if (path == null || !Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new ImportException("The wordlist file was not found or cannot be read.");
        }

        String sourceName = path.getFileName().toString();
        int inserted = 0;
        int duplicates = 0;
        int blank = 0;
        int skipped = 0;
        Set<String> batch = new LinkedHashSet<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String entry = stripTrailingLineBreaks(line);
                if (entry.isEmpty()) {
                    blank++;
                    continue;
                }
                if (entry.getBytes(StandardCharsets.UTF_8).length > MAX_ENTRY_LENGTH) {
                    skipped++;
                    continue;
                }
                if (!batch.add(entry)) {
                    duplicates++;
                    continue;
                }
                if (batch.size() >= BATCH_SIZE) {
                    inserted += flush(batch, sourceName);
                }
            }
            inserted += flush(batch, sourceName);
        } catch (IOException e) {
            throw new ImportException("The wordlist file could not be read to the end.");
        }

        RefreshResult refresh = refreshCredentials();
        return new ImportResult(inserted, duplicates, blank, skipped, refresh.checked(), refresh.skipped());
    }

    private int flush(Set<String> batch, String sourceName) {
        if (batch.isEmpty()) {
            return 0;
        }
        List<String> values = new ArrayList<>(batch);
        int inserted = wordlist.insertIgnore(values, sourceName);
        batch.clear();
        return inserted;
    }

    public RefreshResult refreshCredentials() {
        int checked = 0;
        int skipped = 0;
        for (Credential credential : credentials.findAllForRecheck()) {
            try {
                String password = encryption.decrypt(credential.getPasswordEncrypted());
                credentials.updateWordlistStatus(credential.getId(), matches(password), Instant.now());
                checked++;
            } catch (CryptoException e) {
                skipped++;
            }
        }
        return new RefreshResult(checked, skipped);
    }

    private static String stripTrailingLineBreaks(String line) {
        int end = line.length();
        while (end > 0 && (line.charAt(end - 1) == '\r' || line.charAt(end - 1) == '\n')) {
            end--;
        }
        return line.substring(0, end);
    }

    public record ImportResult(int inserted, int duplicates, int blank, int skipped,
                               int checked, int skippedCredentials) {
    }

    public record RefreshResult(int checked, int skipped) {
    }

    public static final class ImportException extends RuntimeException {
        public ImportException(String message) {
            super(message);
        }
    }
}
