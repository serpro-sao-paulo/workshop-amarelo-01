package br.gov.sifap.pagamento.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.gov.sifap.config.SecurityConfig;
import br.gov.sifap.pagamento.application.ConciliacaoService;
import br.gov.sifap.pagamento.application.FolhaService;
import br.gov.sifap.pagamento.application.PagamentoConsultaService;
import br.gov.sifap.pagamento.domain.Pagamento;
import br.gov.sifap.pagamento.domain.TipoPagamento;
import br.gov.sifap.shared.CPF;
import br.gov.sifap.shared.Money;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/** Contract tests for /api/v1/pagamentos (folha, conciliação, consulta). */
@WebMvcTest(PagamentoController.class)
@Import(SecurityConfig.class)
@WithMockUser
class PagamentoContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FolhaService folhaService;

    @MockBean
    private ConciliacaoService conciliacaoService;

    @MockBean
    private PagamentoConsultaService consultaService;

    private Pagamento pagamentoExemplo() {
        return Pagamento.gerar(
                java.util.UUID.randomUUID(),
                java.util.UUID.randomUUID(),
                CPF.of("52998224725"),
                YearMonth.of(2026, 6),
                Money.of("1000.00"),
                Money.ZERO,
                Money.ZERO,
                TipoPagamento.fromCodigo("M"),
                List.of());
    }

    @Test
    void postFolhaRetorna201ComCpfMascarado() throws Exception { // REQ-025
        when(folhaService.gerarFolha(any(), any())).thenReturn(List.of(pagamentoExemplo()));

        String body = """
                {"competencia":"2026-06","programaId":"11111111-1111-1111-1111-111111111111"}
                """;

        mockMvc.perform(post("/api/v1/pagamentos/folha").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].status").value("G"))
                .andExpect(jsonPath("$[0].cpf").value("***.***.247-**"));
    }

    @Test
    void postConciliacaoRetorna204() throws Exception { // REQ-027
        doNothing().when(conciliacaoService).conciliar(any());

        String body = """
                {"itens":[{"pagamentoId":"22222222-2222-2222-2222-222222222222",
                 "codigoRetorno":"00","valorBanco":1000.00,"banco":"001"}]}
                """;

        mockMvc.perform(post("/api/v1/pagamentos/conciliacao").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    void getListaPorCompetenciaRetorna200() throws Exception { // REQ-025
        when(consultaService.listarPorCompetencia(any())).thenReturn(List.of(pagamentoExemplo()));

        mockMvc.perform(get("/api/v1/pagamentos").param("competencia", "2026-06"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].competencia").value("2026-06"));
    }
}
