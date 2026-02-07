package it.codex.notification.events;

import it.codex.notification.delivery.Channel;
import org.springframework.stereotype.Component;

@Component
public class RecipientResolver {

    public String resolveRecipient(String userId, Channel channel) {
        // Replace later with user profile lookup / directory / DB
        return switch (channel) {
            case EMAIL -> userId + "@example.com";
            case SLACK -> "slack-webhook-key-or-channel";
            case PUSH -> "device-token-or-device-id";
        };
    }
}
