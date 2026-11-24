package com.vaultify.security;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory sliding window limiter, keyed by email and client address.
 */
public final class RateLimiter {

    private final Map<String, Deque<Long>> attempts = new ConcurrentHashMap<>();

    public boolean tooManyAttempts(String key, int maxAttempts) {
        Deque<Long> timestamps = attempts.get(key);
        if (timestamps == null) {
            return false;
        }
        synchronized (timestamps) {
            prune(timestamps);
            return timestamps.size() >= maxAttempts;
        }
    }

    public void hit(String key) {
        Deque<Long> timestamps = attempts.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (timestamps) {
            prune(timestamps);
            timestamps.addLast(System.currentTimeMillis());
        }
    }

    public void clear(String key) {
        attempts.remove(key);
    }

    private static void prune(Deque<Long> timestamps) {
        long cutoff = System.currentTimeMillis() - 60_000L;
        while (!timestamps.isEmpty() && timestamps.peekFirst() < cutoff) {
            timestamps.removeFirst();
        }
    }
}
