package br.gov.sifap.auditoria.application;

import java.util.UUID;

/**
 * Audit trail port (ADR-003). The Auditoria context is the ONLY writer of the
 * audit log; other contexts record events exclusively through this port
 * (REQ-029/032).
 */
public interface AuditLog {

    /**
     * Records an append-only audit event.
     *
     * @param acao      action code (REQ-032): IN, AL, CO, CN, DV
     * @param entidade  affected aggregate name
     * @param entidadeId affected aggregate id
     * @param detalhe   optional human-readable detail (mask sensitive data)
     */
    void record(String acao, String entidade, UUID entidadeId, String detalhe);
}
