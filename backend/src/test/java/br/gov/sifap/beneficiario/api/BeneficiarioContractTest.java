package br.gov.sifap.beneficiario.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.gov.sifap.beneficiario.application.BeneficiarioService;
import br.gov.sifap.beneficiario.domain.Beneficiario;
import br.gov.sifap.config.SecurityConfig;
import br.gov.sifap.shared.CPF;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/** Contract tests for /api/v1/beneficiarios against contracts/openapi.yaml. */
@WebMvcTest(BeneficiarioController.class)
@Import(SecurityConfig.class)
@WithMockUser
class BeneficiarioContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BeneficiarioService service;

    @Test
    void postRetorna201ComCpfMascarado() throws Exception {
        Beneficiario b = Beneficiario.novo(
                CPF.of("52998224725"), "Maria Silva", LocalDate.of(1980, 5, 10), "01");
        when(service.criar(any())).thenReturn(b);

        String body = """
                {"cpf":"529.982.247-25","nome":"Maria Silva","dataNascimento":"1980-05-10","regiao":"01"}
                """;

        mockMvc.perform(post("/api/v1/beneficiarios").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.situacao").value("A"))
                .andExpect(jsonPath("$.cpf").value("***.***.247-**"));
    }

    @Test
    void postSemNomeRetorna400() throws Exception {
        String body = """
                {"cpf":"529.982.247-25","dataNascimento":"1980-05-10"}
                """;

        mockMvc.perform(post("/api/v1/beneficiarios").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getListaRetorna200() throws Exception {
        Page<Beneficiario> page = new PageImpl<>(java.util.List.of(
                Beneficiario.novo(CPF.of("52998224725"), "Maria Silva", LocalDate.of(1980, 5, 10), "01")));
        when(service.listar(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/beneficiarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cpf").value("***.***.247-**"));
    }
}
