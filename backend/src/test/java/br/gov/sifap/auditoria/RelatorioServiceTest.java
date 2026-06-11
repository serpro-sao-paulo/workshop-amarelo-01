package br.gov.sifap.auditoria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.gov.sifap.auditoria.application.RelatorioService;
import org.junit.jupiter.api.Test;

/** Unit tests for report status labels (REQ-030/031). */
class RelatorioServiceTest {

    private final RelatorioService service = new RelatorioService();

    @Test
    void traduzStatusPagamentoPago() { // REQ-030
        assertThat(service.traduzirStatusPagamento("P")).isEqualTo("Pago");
    }

    @Test
    void sinalizaStatusPagamentoDesconhecido() { // REQ-030 (não silencia como GERADO)
        assertThatThrownBy(() -> service.traduzirStatusPagamento("Z"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("STATUS DESCONHECIDO");
    }

    @Test
    void traduzStatusBeneficiario() { // REQ-031
        assertThat(service.traduzirStatusBeneficiario("S")).isEqualTo("Suspenso");
        assertThat(service.traduzirStatusBeneficiario("D")).isEqualTo("Desligado");
    }

    @Test
    void rotulosCobremDominioDePagamento() { // REQ-030
        assertThat(service.rotulosStatusPagamento())
                .containsEntry("G", "Gerado")
                .containsEntry("P", "Pago")
                .containsEntry("C", "Cancelado")
                .containsEntry("D", "Devolvido")
                .containsEntry("E", "Estornado");
    }
}
