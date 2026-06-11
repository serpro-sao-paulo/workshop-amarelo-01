package br.gov.sifap.beneficiario.application;

import br.gov.sifap.shared.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Published when a beneficiary is registered (consumed by Auditoria — ADR-003/004). */
public record BeneficiarioCadastradoEvent(UUID beneficiarioId, Instant occurredAt) implements DomainEvent {

    public static BeneficiarioCadastradoEvent of(UUID beneficiarioId) {
        return new BeneficiarioCadastradoEvent(beneficiarioId, Instant.now());
    }
}
