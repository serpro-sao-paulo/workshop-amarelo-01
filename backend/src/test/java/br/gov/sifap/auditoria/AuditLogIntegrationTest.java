package br.gov.sifap.auditoria;

import static org.assertj.core.api.Assertions.assertThat;

import br.gov.sifap.auditoria.application.AuditLog;
import br.gov.sifap.auditoria.application.AuditoriaConsultaService;
import br.gov.sifap.auditoria.domain.AcaoAuditoria;
import br.gov.sifap.auditoria.domain.EventoAuditoria;
import br.gov.sifap.auditoria.infrastructure.EventoAuditoriaRepository;
import br.gov.sifap.support.AbstractIntegrationTest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Integration tests for the append-only audit trail via the AuditLog port (REQ-032, ADR-003). */
class AuditLogIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AuditLog auditLog;

    @Autowired
    private AuditoriaConsultaService consultaService;

    @Autowired
    private EventoAuditoriaRepository repository;

    @Test
    void gravaEventoSomenteViaPorta() { // REQ-032 / ADR-003
        UUID entidadeId = UUID.randomUUID();

        auditLog.record("CO", "PAGAMENTO", entidadeId, "Pagamento conciliado");

        List<EventoAuditoria> trilha = consultaService.trilhaDe("PAGAMENTO", entidadeId);
        assertThat(trilha).hasSize(1);
        assertThat(trilha.get(0).getAcao()).isEqualTo(AcaoAuditoria.CONCILIACAO);
        assertThat(trilha.get(0).getDetalhe()).isEqualTo("Pagamento conciliado");
        assertThat(trilha.get(0).getMomento()).isNotNull();
    }

    @Test
    void trilhaEhAppendOnly() { // REQ-032 — múltiplos registros acumulam, nada é sobrescrito
        UUID entidadeId = UUID.randomUUID();

        auditLog.record("IN", "BENEFICIARIO", entidadeId, "Cadastrado");
        auditLog.record("AL", "BENEFICIARIO", entidadeId, "Alterado");
        auditLog.record("DV", "BENEFICIARIO", entidadeId, "Divergência");

        assertThat(repository.findByEntidadeAndEntidadeIdOrderByMomentoAsc("BENEFICIARIO", entidadeId))
                .hasSize(3);
    }
}
