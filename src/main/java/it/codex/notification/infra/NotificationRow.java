package it.codex.notification.infra;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("notification")
public record NotificationRow(
        @Id UUID id,
        UUID eventId,
        String userId,
        String tenantId,
        String type,
        Instant createdAt
) {}
