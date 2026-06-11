package br.gov.sifap.auditoria.application;

import br.gov.sifap.beneficiario.application.BeneficiarioCadastradoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Materializes domain events into the audit trail (ADR-003/004).
 *
 * <p>Other contexts publish in-process domain events; the Auditoria context is
 * the only writer of the trail and records them via {@link AuditLog}.
 */
@Component
public class AuditoriaEventListener {

    private final AuditLog auditLog;

    public AuditoriaEventListener(AuditLog auditLog) {
        this.auditLog = auditLog;
    }

    /** REQ-032: register an inclusion ({@code IN}) when a beneficiary is created. */
    @EventListener
    public void onBeneficiarioCadastrado(BeneficiarioCadastradoEvent event) {
        auditLog.record("IN", "BENEFICIARIO", event.beneficiarioId(), "Beneficiário cadastrado");
    }
}
