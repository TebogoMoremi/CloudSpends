package com.tebogo.cloudspend_api.exception;

import java.time.Instant;

public record ApiError(
        int status,
        String error,
        String message,
        Instant timestamp
) {
}