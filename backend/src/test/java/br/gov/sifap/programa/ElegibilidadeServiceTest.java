package br.gov.sifap.programa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.gov.sifap.beneficiario.application.BeneficiarioElegibilidadeView;
import br.gov.sifap.beneficiario.application.BeneficiarioQuery;
import br.gov.sifap.beneficiario.domain.SituacaoBeneficiario;
import br.gov.sifap.programa.application.AvaliarElegibilidadeRequest;
import br.gov.sifap.programa.application.ElegibilidadeResultado;
import br.gov.sifap.programa.application.ElegibilidadeService;
import br.gov.sifap.programa.domain.CriteriosElegibilidade;
import br.gov.sifap.programa.domain.ProgramaSocial;
import br.gov.sifap.programa.domain.StatusPrograma;
import br.gov.sifap.programa.domain.TipoPrograma;
import br.gov.sifap.programa.infrastructure.ProgramaSocialRepository;
import br.gov.sifap.shared.Money;
import br.gov.sifap.shared.error.ValidationException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for eligibility rules (REQ-013..017). */
@ExtendWith(MockitoExtension.class)
class ElegibilidadeServiceTest {

    @Mock
    private ProgramaSocialRepository programas;

    @Mock
    private BeneficiarioQuery beneficiarios;

    private ElegibilidadeService service() {
        return new ElegibilidadeService(programas, beneficiarios);
    }

    private ProgramaSocial programa(
            TipoPrograma tipo, int idadeMin, int idadeMax, Money rendaMax, StatusPrograma status) {
        ProgramaSocial p = ProgramaSocial.novo(
                "PRG", "Programa", tipo,
                new CriteriosElegibilidade(idadeMin, idadeMax, rendaMax),
                LocalDate.of(2020, 1, 1), null);
        if (status == StatusPrograma.INATIVO) {
            setInativo(p);
        }
        return p;
    }

    private void setInativo(ProgramaSocial p) {
        try {
            Field f = ProgramaSocial.class.getDeclaredField("situacao");
            f.setAccessible(true);
            f.set(p, StatusPrograma.INATIVO);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private AvaliarElegibilidadeRequest req(UUID id, Money renda) {
        return new AvaliarElegibilidadeRequest(id, "PRG", renda);
    }

    @Test
    void rejeitaProgramaInativo() { // REQ-013
        when(programas.findByCodigo("PRG")).thenReturn(
                Optional.of(programa(TipoPrograma.TRABALHO, 0, 0, null, StatusPrograma.INATIVO)));

        assertThatThrownBy(() -> service().avaliar(req(UUID.randomUUID(), null)))
                .isInstanceOf(ValidationException.class)
                .hasMessage("PROGRAMA INATIVO");
    }

    @Test
    void inelegivelPorBeneficiarioSuspenso() { // REQ-014
        UUID id = UUID.randomUUID();
        when(programas.findByCodigo("PRG")).thenReturn(
                Optional.of(programa(TipoPrograma.ASSISTENCIAL, 0, 0, null, StatusPrograma.ATIVO)));
        when(beneficiarios.dadosElegibilidade(id)).thenReturn(Optional.of(
                new BeneficiarioElegibilidadeView(
                        SituacaoBeneficiario.SUSPENSO, LocalDate.of(1980, 1, 1))));

        ElegibilidadeResultado r = service().avaliar(req(id, null));

        assertThat(r.elegivel()).isFalse();
        assertThat(r.motivos()).contains("SUSPENSO");
    }

    @Test
    void inelegivelAbaixoDaIdadeMinima() { // REQ-015
        UUID id = UUID.randomUUID();
        when(programas.findByCodigo("PRG")).thenReturn(
                Optional.of(programa(TipoPrograma.ASSISTENCIAL, 18, 0, null, StatusPrograma.ATIVO)));
        when(beneficiarios.dadosElegibilidade(id)).thenReturn(Optional.of(
                new BeneficiarioElegibilidadeView(
                        SituacaoBeneficiario.ATIVO, LocalDate.now().minusYears(17))));

        ElegibilidadeResultado r = service().avaliar(req(id, null));

        assertThat(r.elegivel()).isFalse();
        assertThat(r.motivos()).contains("IDADE INFERIOR A MINIMA");
    }

    @Test
    void inelegivelRendaExcedeTeto() { // REQ-016
        UUID id = UUID.randomUUID();
        when(programas.findByCodigo("PRG")).thenReturn(Optional.of(
                programa(TipoPrograma.ASSISTENCIAL, 0, 0, Money.of("1000.00"), StatusPrograma.ATIVO)));
        when(beneficiarios.dadosElegibilidade(id)).thenReturn(Optional.of(
                new BeneficiarioElegibilidadeView(
                        SituacaoBeneficiario.ATIVO, LocalDate.of(1980, 1, 1))));

        ElegibilidadeResultado r = service().avaliar(req(id, Money.of("1200.00")));

        assertThat(r.elegivel()).isFalse();
        assertThat(r.motivos()).contains("RENDA FAMILIAR EXCEDE O TETO");
    }

    @Test
    void inelegivelPrevidenciarioAbaixoDe60() { // REQ-017
        UUID id = UUID.randomUUID();
        when(programas.findByCodigo("PRG")).thenReturn(Optional.of(
                programa(TipoPrograma.PREVIDENCIARIO, 0, 0, null, StatusPrograma.ATIVO)));
        when(beneficiarios.dadosElegibilidade(id)).thenReturn(Optional.of(
                new BeneficiarioElegibilidadeView(
                        SituacaoBeneficiario.ATIVO, LocalDate.now().minusYears(58))));

        ElegibilidadeResultado r = service().avaliar(req(id, null));

        assertThat(r.elegivel()).isFalse();
        assertThat(r.motivos()).contains("IDADE MINIMA 60 PARA PREVIDENCIARIO");
    }

    @Test
    void elegivelQuandoTodosCriteriosAtendidos() {
        UUID id = UUID.randomUUID();
        when(programas.findByCodigo("PRG")).thenReturn(Optional.of(
                programa(TipoPrograma.TRABALHO, 16, 65, Money.of("2000.00"), StatusPrograma.ATIVO)));
        when(beneficiarios.dadosElegibilidade(id)).thenReturn(Optional.of(
                new BeneficiarioElegibilidadeView(
                        SituacaoBeneficiario.ATIVO, LocalDate.now().minusYears(30))));

        ElegibilidadeResultado r = service().avaliar(req(id, Money.of("1000.00")));

        assertThat(r.elegivel()).isTrue();
        assertThat(r.motivos()).isEmpty();
    }
}
