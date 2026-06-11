package br.gov.sifap.shared.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Thin wrapper over Spring's {@link ApplicationEventPublisher} so bounded
 * contexts depend on a domain-level abstraction instead of the framework
 * directly (ADR-004).
 */
@Component
public class DomainEventPublisher {

    private final ApplicationEventPublisher delegate;

    public DomainEventPublisher(ApplicationEventPublisher delegate) {
        this.delegate = delegate;
    }

    public void publish(DomainEvent event) {
        delegate.publishEvent(event);
    }
}
