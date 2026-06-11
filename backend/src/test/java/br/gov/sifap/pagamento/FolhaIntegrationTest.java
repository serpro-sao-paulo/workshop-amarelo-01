package br.gov.sifap.pagamento;

import static org.assertj.core.api.Assertions.assertThat;

import br.gov.sifap.beneficiario.api.CriarBeneficiarioRequest;
import br.gov.sifap.beneficiario.application.BeneficiarioFolhaView;
import br.gov.sifap.beneficiario.application.BeneficiarioService;
import br.gov.sifap.pagamento.application.FolhaService;
import br.gov.sifap.support.AbstractIntegrationTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Integration tests for payroll selection ordering and active-only rule (REQ-025/026). */
class FolhaIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private BeneficiarioService beneficiarioService;

    @Autowired
    private FolhaService folhaService;

    @Test
    void selecionaApenasAtivosOrdenadosPorCpf() { // REQ-025/026
        // CPFs válidos inseridos fora de ordem.
        beneficiarioService.criar(new CriarBeneficiarioRequest(
                "529.982.247-25", "Maria", LocalDate.of(1980, 5, 10), "01", List.of()));
        beneficiarioService.criar(new CriarBeneficiarioRequest(
                "111.444.777-35", "Carlos", LocalDate.of(1990, 7, 7), "02", List.of()));
        beneficiarioService.criar(new CriarBeneficiarioRequest(
                "168.995.350-09", "Ana", LocalDate.of(1975, 3, 2), "03", List.of()));

        List<BeneficiarioFolhaView> folha = folhaService.selecionarParaFolha();

        List<String> cpfs = folha.stream().map(b -> b.cpf().unmasked()).toList();
        assertThat(cpfs).containsExactly("11144477735", "16899535009", "52998224725"); // REQ-025
    }
}
