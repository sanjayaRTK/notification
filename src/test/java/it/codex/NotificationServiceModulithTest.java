package it.codex;

import it.codex.notification.NotificationApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class NotificationServiceModulithTest {

    @Test
    void verifiesModularStructure() {
        ApplicationModules.of(NotificationApplication.class).verify();
    }
}
