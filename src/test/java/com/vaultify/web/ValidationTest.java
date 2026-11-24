package com.vaultify.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationTest {

    @Test
    void validatesEmailAddresses() {
        assertTrue(Validation.isEmail("person@example.com"));
        assertTrue(Validation.isEmail("first.last+tag@sub.example.co"));

        assertFalse(Validation.isEmail("person"));
        assertFalse(Validation.isEmail("person@"));
        assertFalse(Validation.isEmail("@example.com"));
        assertFalse(Validation.isEmail("person@example"));
        assertFalse(Validation.isEmail("person example@example.com"));
        assertFalse(Validation.isEmail(null));
    }

    @Test
    void validatesUrls() {
        assertTrue(Validation.isUrl("https://example.com"));
        assertTrue(Validation.isUrl("http://example.com/login?next=1"));

        assertFalse(Validation.isUrl("example.com"));
        assertFalse(Validation.isUrl("javascript:alert(1)"));
        assertFalse(Validation.isUrl("https://"));
        assertFalse(Validation.isUrl(""));
        assertFalse(Validation.isUrl(null));
    }

    @Test
    void detectsBlankValues() {
        assertTrue(Validation.isBlank(null));
        assertTrue(Validation.isBlank(""));
        assertTrue(Validation.isBlank("   "));
        assertFalse(Validation.isBlank("value"));
    }
}
