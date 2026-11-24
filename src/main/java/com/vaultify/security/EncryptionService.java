package com.vaultify.security;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Encrypts credential secrets with AES-256-GCM. Output format: {@code v1:} followed
 * by base64 of the 12 byte IV and the ciphertext (which includes the GCM tag).
 */
public final class EncryptionService {

    private static final String COLON = ":";
    private static final String VERSION = "v1";
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final int KEY_BYTES = 32;

    private final SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();

    public EncryptionService(String configuredKey) {
        this.key = new SecretKeySpec(resolveKey(configuredKey), "AES");
    }

    private static byte[] resolveKey(String configuredKey) {
        if (configuredKey == null || configuredKey.isBlank()) {
            throw new IllegalStateException(
                    "VAULTIFY_APP_KEY (or APP_KEY) is required; use a base64 encoded 32 byte key");
        }
        String value = configuredKey.strip();
        if (value.startsWith("base64:")) {
            value = value.substring("base64:".length());
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(value);
            if (decoded.length == KEY_BYTES) {
                return decoded;
            }
        } catch (IllegalArgumentException ignored) {
            // Not base64, derive a key from the raw text below.
        }
        return sha256(value.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] sha256(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    public String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }
        try {
            byte[] iv = new byte[IV_BYTES];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer payload = ByteBuffer.allocate(iv.length + cipherText.length);
            payload.put(iv);
            payload.put(cipherText);
            return VERSION + COLON + Base64.getEncoder().encodeToString(payload.array());
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Could not encrypt value", e);
        }
    }

    public String decrypt(String encoded) {
        if (encoded == null) {
            return null;
        }
        if (!encoded.startsWith(VERSION + COLON)) {
            throw new CryptoException("Unsupported ciphertext format");
        }
        byte[] payload;
        try {
            payload = Base64.getDecoder().decode(encoded.substring((VERSION + COLON).length()));
        } catch (IllegalArgumentException e) {
            throw new CryptoException("Ciphertext is not valid base64", e);
        }
        if (payload.length <= IV_BYTES) {
            throw new CryptoException("Ciphertext is too short");
        }
        try {
            byte[] iv = new byte[IV_BYTES];
            byte[] cipherText = new byte[payload.length - IV_BYTES];
            System.arraycopy(payload, 0, iv, 0, IV_BYTES);
            System.arraycopy(payload, IV_BYTES, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Could not decrypt value", e);
        }
    }
}
