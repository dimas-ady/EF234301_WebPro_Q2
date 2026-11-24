package com.vaultify.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    private final PasswordHasher hasher = new PasswordHasher();

    @Test
    void hashesAreSaltedAndVerifiable() {
        String first = hasher.hash("correct horse battery staple");
        String second = hasher.hash("correct horse battery staple");

        assertNotEquals(first, second, "each hash must use a new salt");
        assertTrue(first.startsWith("pbkdf2_sha256$210000$"));
        assertTrue(hasher.verify("correct horse battery staple", first));
        assertTrue(hasher.verify("correct horse battery staple", second));
    }

    @Test
    void rejectsWrongPasswordAndMalformedStoredValue() {
        String stored = hasher.hash("secret-password");

        assertTrue(!hasher.verify("secret-passwor", stored));
        assertTrue(!hasher.verify("secret-password", "plaintext"));
        assertTrue(!hasher.verify("secret-password", null));
        assertTrue(!hasher.verify(null, stored));
    }

    @Test
    void doesNotStorePlaintext() {
        String password = "uniquely-recognisable-value";
        String stored = hasher.hash(password);

        assertTrue(!stored.contains(password), "the stored hash must not contain the raw password");
        assertEquals(4, stored.split("\\$").length);
    }
}
