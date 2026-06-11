package br.gov.sifap.pagamento;

import static org.assertj.core.api.Assertions.assertThat;

import br.gov.sifap.calculo.application.DescontoResultado;
import br.gov.sifap.calculo.application.DescontoService;
import br.gov.sifap.pagamento.domain.Desconto;
import br.gov.sifap.pagamento.domain.TipoDesconto;
import br.gov.sifap.shared.Money;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests for discount rules (REQ-021/022/023). */
class DescontoServiceTest {

    private final DescontoService service = new DescontoService();

    private Desconto ordinario(String valor) {
        return new Desconto(TipoDesconto.ORDINARIO, Money.of(valor), "teste");
    }

    private Desconto judicial(String valor) {
        return new Desconto(TipoDesconto.JUDICIAL, Money.of(valor), "ordem judicial");
    }

    @Test
    void tetoDe30PorCentoLimitaDescontoOrdinario() { // REQ-021/023
        DescontoResultado r = service.aplicar(Money.of("1000.00"), List.of(ordinario("400.00")));

        assertThat(r.teto()).isEqualTo(Money.of("300.00"));
        assertThat(r.tetoAplicado()).isTrue();
        assertThat(r.total()).isEqualTo(Money.of("300.00"));
    }

    @Test
    void descontoOrdinarioAbaixoDoTetoNaoEhLimitado() { // REQ-023
        DescontoResultado r = service.aplicar(Money.of("1000.00"), List.of(ordinario("200.00")));

        assertThat(r.tetoAplicado()).isFalse();
        assertThat(r.total()).isEqualTo(Money.of("200.00"));
    }

    @Test
    void descontoJudicialExcedeOTeto() { // REQ-022
        DescontoResultado r = service.aplicar(Money.of("1000.00"), List.of(judicial("500.00")));

        assertThat(r.totalJudicial()).isEqualTo(Money.of("500.00"));
        assertThat(r.total()).isEqualTo(Money.of("500.00"));
    }

    @Test
    void judicialSemTetoSomadoAoOrdinarioLimitado() { // REQ-021/022/023
        DescontoResultado r = service.aplicar(
                Money.of("1000.00"), List.of(judicial("500.00"), ordinario("400.00")));

        // judicial 500 (uncapped) + ordinário capped at 300 = 800
        assertThat(r.totalJudicial()).isEqualTo(Money.of("500.00"));
        assertThat(r.totalOrdinario()).isEqualTo(Money.of("300.00"));
        assertThat(r.total()).isEqualTo(Money.of("800.00"));
    }
}
