package br.gov.sifap.programa.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.gov.sifap.config.SecurityConfig;
import br.gov.sifap.programa.application.ElegibilidadeResultado;
import br.gov.sifap.programa.application.ElegibilidadeService;
import br.gov.sifap.programa.application.ProgramaSocialService;
import br.gov.sifap.programa.domain.CriteriosElegibilidade;
import br.gov.sifap.programa.domain.ProgramaSocial;
import br.gov.sifap.programa.domain.TipoPrograma;
import br.gov.sifap.shared.Money;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/** Contract tests for /api/v1/programas and /api/v1/elegibilidade. */
@WebMvcTest({ProgramaController.class, ElegibilidadeController.class})
@Import(SecurityConfig.class)
@WithMockUser
class ProgramaContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProgramaSocialService programaService;

    @MockBean
    private ElegibilidadeService elegibilidadeService;

    @Test
    void postProgramaRetorna201ComStatusAtivo() throws Exception { // REQ-012
        ProgramaSocial programa = ProgramaSocial.novo(
                "BF", "Bolsa Família", TipoPrograma.fromCodigo("P"),
                new CriteriosElegibilidade(60, 0, Money.of("1000.00")), null, null);
        when(programaService.criar(any())).thenReturn(programa);

        String body = """
                {"codigo":"BF","nome":"Bolsa Família","tipo":"P","idadeMinima":60,
                 "idadeMaxima":0,"rendaMaxima":1000.00}
                """;

        mockMvc.perform(post("/api/v1/programas").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.situacao").value("A")) // REQ-012
                .andExpect(jsonPath("$.tipo").value("P"));
    }

    @Test
    void postProgramaSemCodigoRetorna400() throws Exception {
        String body = """
                {"nome":"Bolsa Família","tipo":"P"}
                """;

        mockMvc.perform(post("/api/v1/programas").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postElegibilidadeRetornaResultadoComMotivos() throws Exception { // REQ-016
        when(elegibilidadeService.avaliar(any()))
                .thenReturn(ElegibilidadeResultado.de(List.of("RENDA ACIMA DO TETO")));

        String body = """
                {"beneficiarioId":"11111111-1111-1111-1111-111111111111",
                 "programaCodigo":"BF","rendaFamiliar":5000.00}
                """;

        mockMvc.perform(post("/api/v1/elegibilidade").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.elegivel").value(false))
                .andExpect(jsonPath("$.motivos[0]").value("RENDA ACIMA DO TETO"));
    }
}
