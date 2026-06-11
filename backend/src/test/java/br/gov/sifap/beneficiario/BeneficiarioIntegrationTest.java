package br.gov.sifap.beneficiario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.gov.sifap.beneficiario.api.CriarBeneficiarioRequest;
import br.gov.sifap.beneficiario.api.DependenteRequest;
import br.gov.sifap.beneficiario.application.BeneficiarioService;
import br.gov.sifap.beneficiario.domain.Beneficiario;
import br.gov.sifap.beneficiario.domain.SituacaoBeneficiario;
import br.gov.sifap.shared.error.ConflictException;
import br.gov.sifap.support.AbstractIntegrationTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/** Integration tests for beneficiary registration (REQ-006/007/009/010). */
@TestPropertySource(properties = "sifap.beneficiario.limite-dependentes=2")
class BeneficiarioIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private BeneficiarioService service;

    @Test
    void cadastraComStatusInicialAtivoEDependentes() {
        CriarBeneficiarioRequest request = new CriarBeneficiarioRequest(
                "529.982.247-25", "Maria Silva", LocalDate.of(1980, 5, 10), "01",
                List.of(new DependenteRequest("João Silva", LocalDate.of(2010, 1, 1), "FI")));

        Beneficiario salvo = service.criar(request);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getSituacao()).isEqualTo(SituacaoBeneficiario.ATIVO); // REQ-006
        assertThat(salvo.getDependentes()).hasSize(1);                          // ADR-002
    }

    @Test
    void rejeitaCpfDuplicado() {
        CriarBeneficiarioRequest request = new CriarBeneficiarioRequest(
                "168.995.350-09", "Ana Souza", LocalDate.of(1975, 3, 2), "02", List.of());
        service.criar(request);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("BENEFICIARIO JA CADASTRADO"); // REQ-007
    }

    @Test
    void rejeitaAcimaDoLimiteDeDependentes() {
        CriarBeneficiarioRequest request = new CriarBeneficiarioRequest(
                "111.444.777-35", "Carlos Lima", LocalDate.of(1990, 7, 7), "03",
                List.of(
                        new DependenteRequest("Dep 1", null, "FI"),
                        new DependenteRequest("Dep 2", null, "FI"),
                        new DependenteRequest("Dep 3", null, "FI"))); // limite=2

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("LIMITE DE DEPENDENTES ATINGIDO"); // REQ-010
    }
}
