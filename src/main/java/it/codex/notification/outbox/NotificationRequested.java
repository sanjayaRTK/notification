package it.codex.notification.outbox;

import it.codex.notification.delivery.Channel;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record NotificationRequested(
        UUID notificationId,
        UUID deliveryId,
        UUID eventId,
        String userId,
        String tenantId,
        Channel channel,
        String recipient,
        String templateKey,
        int templateVersion,
        Map<String, Object> variables,
        int attempt,
        Instant createdAt,
        int version
) {}
