package it.codex.notification.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record BusinessEvent(
        @NotNull UUID eventId,
        @NotBlank String eventType,
        @NotNull Instant occurredAt,
        @NotBlank String userId,
        String tenantId,
        @NotNull Map<String, Object> data,
        int version
) {}
