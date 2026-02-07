package it.codex.notification.api;

import it.codex.notification.events.IngestionService;
import it.codex.notification.events.BusinessEvent;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventsController {

    private final IngestionService ingestionService;

    public EventsController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping
    public ResponseEntity<CreateEventResponse> ingest(@Valid @RequestBody BusinessEvent event) {
        UUID notificationId = ingestionService.ingest(event);
        return ResponseEntity.ok(new CreateEventResponse(notificationId));
    }

    public record CreateEventResponse(UUID notificationId) {}
}
