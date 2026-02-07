package it.codex.notification.infra;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OutboxRepository extends CrudRepository<OutboxRow, UUID> {}
