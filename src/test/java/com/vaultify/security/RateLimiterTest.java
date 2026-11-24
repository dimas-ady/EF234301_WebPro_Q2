package com.vaultify.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    @Test
    void blocksAfterTheConfiguredNumberOfAttempts() {
        RateLimiter limiter = new RateLimiter();
        String key = "person@example.com|127.0.0.1";

        for (int attempt = 0; attempt < 5; attempt++) {
            assertFalse(limiter.tooManyAttempts(key, 5));
            limiter.hit(key);
        }

        assertTrue(limiter.tooManyAttempts(key, 5));
    }

    @Test
    void clearResetsTheWindow() {
        RateLimiter limiter = new RateLimiter();
        String key = "person@example.com|127.0.0.1";

        limiter.hit(key);
        limiter.hit(key);
        limiter.clear(key);

        assertFalse(limiter.tooManyAttempts(key, 2));
    }

    @Test
    void keysAreIndependent() {
        RateLimiter limiter = new RateLimiter();

        limiter.hit("one|1.1.1.1");
        limiter.hit("one|1.1.1.1");

        assertTrue(limiter.tooManyAttempts("one|1.1.1.1", 2));
        assertFalse(limiter.tooManyAttempts("two|1.1.1.1", 2));
    }
}
