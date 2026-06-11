package br.gov.sifap.auditoria.api;

import br.gov.sifap.auditoria.domain.EventoAuditoria;
import java.time.Instant;
import java.util.UUID;

/** Audit event representation returned by the API (REQ-032). */
public record EventoAuditoriaResponse(
        UUID id,
        String acao,
        String acaoRotulo,
        String entidade,
        UUID entidadeId,
        String detalhe,
        Instant momento) {

    public static EventoAuditoriaResponse from(EventoAuditoria e) {
        return new EventoAuditoriaResponse(
                e.getId(),
                e.getAcao().codigo(),
                e.getAcao().rotulo(),
                e.getEntidade(),
                e.getEntidadeId(),
                e.getDetalhe(),
                e.getMomento());
    }
}
