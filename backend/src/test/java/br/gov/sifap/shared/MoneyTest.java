package br.gov.sifap.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Unit tests for monetary truncation (REQ-019). */
class MoneyTest {

    @Test
    void truncaParaDuasCasasSemArredondar() {
        assertThat(Money.of("10.999").toBigDecimal().toPlainString()).isEqualTo("10.99");
        assertThat(Money.of("10.991").toBigDecimal().toPlainString()).isEqualTo("10.99");
    }

    @Test
    void somaTruncada() {
        assertThat(Money.of("1.555").add(Money.of("1.555")).toString()).isEqualTo("3.10");
    }

    @Test
    void percentualTruncado() {
        assertThat(Money.of("100.00").percentage(30).toString()).isEqualTo("30.00");
    }
}
