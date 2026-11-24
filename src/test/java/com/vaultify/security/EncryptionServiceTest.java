package com.vaultify.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EncryptionServiceTest {

    private final EncryptionService encryption = new EncryptionService("test-key-material");

    @Test
    void roundTripsValues() {
        String cipherText = encryption.encrypt("hunter2");

        assertNotEquals("hunter2", cipherText);
        assertEquals("hunter2", encryption.decrypt(cipherText));
    }

    @Test
    void usesFreshIvForEachValue() {
        assertNotEquals(encryption.encrypt("hunter2"), encryption.encrypt("hunter2"));
    }

    @Test
    void nullStaysNull() {
        assertNull(encryption.encrypt(null));
        assertNull(encryption.decrypt(null));
    }

    @Test
    void rejectsTamperedPayload() {
        String cipherText = encryption.encrypt("hunter2");
        String tampered = cipherText.substring(0, cipherText.length() - 2) + "AA";

        assertThrows(CryptoException.class, () -> encryption.decrypt(tampered));
    }

    @Test
    void rejectsUnknownFormat() {
        assertThrows(CryptoException.class, () -> encryption.decrypt("plain:value"));
    }
}
