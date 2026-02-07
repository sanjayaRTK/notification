package it.codex.notification.templates;

import it.codex.notification.delivery.Channel;
import org.springframework.stereotype.Component;

@Component
public class TemplateResolver {
    public String templateKeyFor(String eventType, Channel channel) {
        // start simple; later this will be DB-driven with versions
        return switch (eventType) {
            case "USER_SIGNED_UP" -> "user_signup";
            default -> "generic";
        };
    }

    public int templateVersionFor(String templateKey, Channel channel) {
        return 1;
    }
}
