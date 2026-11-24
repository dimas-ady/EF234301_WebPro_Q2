package com.vaultify.cli;

import com.vaultify.AppContext;
import com.vaultify.config.AppConfig;
import com.vaultify.config.Database;
import com.vaultify.config.SchemaRunner;
import com.vaultify.service.WordlistService;

import java.nio.file.Path;

/**
 * Mirrors the Laravel command {@code php artisan wordlist:import {path}}.
 */
public final class ImportWordlist {

    private ImportWordlist() {
    }

    public static void main(String[] args) {
        if (args.length < 1 || args[0].isBlank()) {
            System.err.println("Usage: wordlist:import <path>");
            System.exit(1);
            return;
        }

        AppConfig config = new AppConfig();
        try (Database database = new Database(config)) {
            new SchemaRunner(database).run();
            AppContext app = new AppContext(database, config.appKey());

            WordlistService.ImportResult result = app.wordlistService().importFile(Path.of(args[0]));

            System.out.printf("Import complete: %d added, %d duplicates, %d blank lines, %d skipped.%n",
                    result.inserted(), result.duplicates(), result.blank(), result.skipped());
            System.out.printf("Credential recheck: %d checked, %d skipped.%n",
                    result.checked(), result.skippedCredentials());
        } catch (WordlistService.ImportException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
