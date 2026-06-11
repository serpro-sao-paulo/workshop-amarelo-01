package br.gov.sifap.auditoria.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.gov.sifap.auditoria.application.AuditoriaConsultaService;
import br.gov.sifap.auditoria.application.RelatorioService;
import br.gov.sifap.auditoria.domain.AcaoAuditoria;
import br.gov.sifap.auditoria.domain.EventoAuditoria;
import br.gov.sifap.config.SecurityConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/** Contract tests for /api/v1/auditoria and /api/v1/relatorios. */
@WebMvcTest({AuditoriaController.class, RelatorioController.class})
@Import(SecurityConfig.class)
@WithMockUser
class AuditoriaContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditoriaConsultaService auditoriaService;

    @MockBean
    private RelatorioService relatorioService;

    @Test
    void getTrilhaRetorna200ComEventos() throws Exception { // REQ-032
        EventoAuditoria evento = EventoAuditoria.registrar(
                AcaoAuditoria.INCLUSAO, "BENEFICIARIO", UUID.randomUUID(), null, "Beneficiário cadastrado");
        when(auditoriaService.trilha()).thenReturn(List.of(evento));

        mockMvc.perform(get("/api/v1/auditoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].acao").value("IN"))
                .andExpect(jsonPath("$[0].acaoRotulo").value("Inclusão"))
                .andExpect(jsonPath("$[0].entidade").value("BENEFICIARIO"));
    }

    @Test
    void getRotulosStatusPagamentoRetorna200() throws Exception { // REQ-030
        when(relatorioService.rotulosStatusPagamento())
                .thenReturn(java.util.Map.of("P", "Pago"));

        mockMvc.perform(get("/api/v1/relatorios/status-pagamento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.P").value("Pago"));
    }
}
