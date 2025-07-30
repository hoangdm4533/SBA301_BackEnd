package com.example.demologin.exception.exceptions;

public class AccountLockedException extends RuntimeException {
    private final long remainingMinutes;

    public AccountLockedException(String message, long remainingMinutes) {
        super(message);
        this.remainingMinutes = remainingMinutes;
    }

    public long getRemainingMinutes() {
        return remainingMinutes;
    }
}
