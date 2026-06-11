package br.gov.sifap.auditoria.application;

import br.gov.sifap.auditoria.domain.AcaoAuditoria;
import br.gov.sifap.auditoria.domain.EventoAuditoria;
import br.gov.sifap.auditoria.infrastructure.EventoAuditoriaRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Append-only implementation of the {@link AuditLog} port (ADR-003, REQ-032).
 *
 * <p>This is the ONLY writer of the audit trail. Events are never updated or
 * deleted.
 */
@Service
public class AuditLogService implements AuditLog {

    private final EventoAuditoriaRepository repository;

    public AuditLogService(EventoAuditoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void record(String acao, String entidade, UUID entidadeId, String detalhe) {
        EventoAuditoria evento = EventoAuditoria.registrar(
                AcaoAuditoria.fromCodigo(acao), entidade, entidadeId, null, detalhe);
        repository.save(evento);
    }
}
