package it.codex.notification.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.codex.notification.delivery.Channel;
import it.codex.notification.infra.DeliveryRepository;
import it.codex.notification.infra.NotificationDeliveryRow;
import it.codex.notification.infra.NotificationRepository;
import it.codex.notification.infra.NotificationRow;
import it.codex.notification.infra.OutboxRepository;
import it.codex.notification.infra.OutboxRow;
import it.codex.notification.outbox.NotificationRequested;
import it.codex.notification.preferences.PreferenceService;
import it.codex.notification.templates.TemplateResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;

@Service
public class IngestionService {

    public static final String TOPIC_NOTIFICATION_REQUESTED_V1 = "notification.requested.v1";

    private final NotificationRepository notificationRepository;
    private final DeliveryRepository deliveryRepository;
    private final OutboxRepository outboxRepository;
    private final PreferenceService preferenceService;
    private final TemplateResolver templateResolver;
    private final RecipientResolver recipientResolver;
    private final ObjectMapper objectMapper;

    public IngestionService(NotificationRepository notificationRepository,
                            DeliveryRepository deliveryRepository,
                            OutboxRepository outboxRepository,
                            PreferenceService preferenceService,
                            TemplateResolver templateResolver,
                            RecipientResolver recipientResolver,
                            ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.deliveryRepository = deliveryRepository;
        this.outboxRepository = outboxRepository;
        this.preferenceService = preferenceService;
        this.templateResolver = templateResolver;
        this.recipientResolver = recipientResolver;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public UUID ingest(BusinessEvent event) {
        Instant now = Instant.now();

        UUID notificationId = UUID.randomUUID();
        notificationRepository.save(new NotificationRow(
                notificationId,
                event.eventId(),
                event.userId(),
                event.tenantId(),
                event.eventType(),
                now
        ));

        EnumSet<Channel> channels = EnumSet.noneOf(Channel.class);
        if (preferenceService.allowEmail(event.userId())) channels.add(Channel.EMAIL);
        if (preferenceService.allowSlack(event.userId())) channels.add(Channel.SLACK);
        if (preferenceService.allowPush(event.userId())) channels.add(Channel.PUSH);

        channels.forEach(channel -> {
            String recipient = recipientResolver.resolveRecipient(event.userId(), channel);
            String templateKey = templateResolver.templateKeyFor(event.eventType(), channel);
            int templateVersion = templateResolver.templateVersionFor(templateKey, channel);
            UUID deliveryId = UUID.randomUUID();
            deliveryRepository.save(new NotificationDeliveryRow(
                    deliveryId,
                    notificationId,
                    channel.name(),
                    recipient,
                    templateKey,
                    templateVersion,
                    "PENDING",
                    0,
                    null,
                    null,
                    null,
                    now,
                    now
            ));
            NotificationRequested msg = new NotificationRequested(
                    notificationId,
                    deliveryId,
                    event.eventId(),
                    event.userId(),
                    event.tenantId(),
                    channel,
                    recipient,
                    templateKey,
                    templateVersion,
                    safeVariables(event.data()),
                    0,
                    now,
                    1
            );
            outboxRepository.save(new OutboxRow(
                    UUID.randomUUID(),
                    TOPIC_NOTIFICATION_REQUESTED_V1,
                    notificationId.toString(),      // key
                    toJson(msg),
                    "NEW",
                    0,
                    null,
                    now,
                    null
            ));
        });

        return notificationId;
    }

    private Map<String, Object> safeVariables(Map<String, Object> data) {
        // You can whitelist keys later; for now pass through.
        return data;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
    }
}
