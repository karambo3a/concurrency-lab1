package org.labs;

public record ApplicationConfig(int programmersCount, int waitersCount, long foodCount) {

    public ApplicationConfig {
        if (programmersCount < 2) {
            throw new IllegalArgumentException("must be at least 2 programmers");
        }

        if (waitersCount <= 0) {
            throw new IllegalArgumentException("waitersCount must be positive");
        }

        if (foodCount < 0) {
            throw new IllegalArgumentException("foodCount must not be negative");
        }
    }
}