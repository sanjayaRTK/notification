package it.codex.notification.infra;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("notification_delivery")
public record NotificationDeliveryRow(
        @Id UUID id,
        UUID notificationId,
        String channel,
        String recipient,
        String templateKey,
        int templateVersion,
        String status,
        int attempts,
        Instant nextAttemptAt,
        String lastError,
        String providerMessageId,
        Instant createdAt,
        Instant updatedAt
) {}
