package br.gov.sifap.shared.events;

import java.time.Instant;

/**
 * Marker for in-process domain events (ADR-004).
 *
 * <p>Events are published with Spring's {@code ApplicationEventPublisher} and
 * consumed in-process. Consumers that must survive failures (e.g. the audit
 * trail) should be backed by a transactional outbox + idempotency.
 */
public interface DomainEvent {

    Instant occurredAt();
}
