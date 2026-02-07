package it.codex.notification.infra;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("notification_outbox")
public record OutboxRow(
        @Id UUID id,
        String topic,
        String key,
        String payload,     // store as JSON string; DB column is jsonb but JDBC can write text
        String status,
        int attempts,
        String lastError,
        Instant createdAt,
        Instant publishedAt
) {}
