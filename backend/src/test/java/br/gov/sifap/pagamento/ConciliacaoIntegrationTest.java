package br.gov.sifap.pagamento;

import static org.assertj.core.api.Assertions.assertThat;

import br.gov.sifap.pagamento.application.ConciliacaoItem;
import br.gov.sifap.pagamento.application.ConciliacaoService;
import br.gov.sifap.pagamento.domain.Pagamento;
import br.gov.sifap.pagamento.domain.StatusPagamento;
import br.gov.sifap.pagamento.domain.TipoPagamento;
import br.gov.sifap.pagamento.infrastructure.PagamentoRepository;
import br.gov.sifap.shared.CPF;
import br.gov.sifap.shared.Money;
import br.gov.sifap.support.AbstractIntegrationTest;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Integration tests for bank reconciliation: status by return code + divergence (REQ-027/028). */
class ConciliacaoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PagamentoRepository pagamentos;

    @Autowired
    private ConciliacaoService conciliacaoService;

    private Pagamento novoPagamento(String valorBase) {
        Pagamento p = Pagamento.gerar(
                UUID.randomUUID(), UUID.randomUUID(), CPF.of("52998224725"),
                YearMonth.of(2026, 6), Money.of(valorBase), Money.ZERO, Money.ZERO,
                TipoPagamento.MENSAL, List.of());
        return pagamentos.save(p);
    }

    @Test
    void codigo00MarcaPagoSemDivergencia() { // REQ-027/028
        Pagamento p = novoPagamento("1000.00");

        conciliacaoService.conciliar(List.of(
                new ConciliacaoItem(p.getId(), "00", Money.of("1000.00"), "001")));

        Pagamento atualizado = pagamentos.findById(p.getId()).orElseThrow();
        assertThat(atualizado.getStatus()).isEqualTo(StatusPagamento.PAGO); // REQ-027
        assertThat(atualizado.isDivergente()).isFalse();                    // REQ-028
    }

    @Test
    void diferencaAcimaDeUmCentavoMarcaDivergente() { // REQ-028
        Pagamento p = novoPagamento("1000.00");

        conciliacaoService.conciliar(List.of(
                new ConciliacaoItem(p.getId(), "00", Money.of("999.90"), "001")));

        Pagamento atualizado = pagamentos.findById(p.getId()).orElseThrow();
        assertThat(atualizado.isDivergente()).isTrue(); // REQ-028
    }

    @Test
    void codigo01MarcaDevolvido() { // REQ-027
        Pagamento p = novoPagamento("500.00");

        conciliacaoService.conciliar(List.of(
                new ConciliacaoItem(p.getId(), "01", Money.of("500.00"), "001")));

        Pagamento atualizado = pagamentos.findById(p.getId()).orElseThrow();
        assertThat(atualizado.getStatus()).isEqualTo(StatusPagamento.DEVOLVIDO);
    }
}
