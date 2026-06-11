package br.gov.sifap.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** Unit tests for CPF modulo-11 validation and masking (REQ-002/003). */
class CPFTest {

    @Test
    void aceitaCpfValido() {
        // 529.982.247-25 is a well-known valid CPF.
        CPF cpf = CPF.of("52998224725");
        assertThat(cpf.unmasked()).isEqualTo("52998224725");
    }

    @Test
    void aceitaCpfFormatado() {
        assertThat(CPF.of("529.982.247-25").unmasked()).isEqualTo("52998224725");
    }

    @Test
    void rejeitaDigitoVerificadorInvalido() {
        assertThatThrownBy(() -> CPF.of("52998224724"))
                .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void rejeitaSequenciaRepetida() {
        assertThatThrownBy(() -> CPF.of("11111111111"))
                .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void rejeitaTamanhoInvalido() {
        assertThatThrownBy(() -> CPF.of("123"))
                .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void mascaraNaoExpoeDigitosIniciais() {
        String masked = CPF.of("52998224725").masked();
        assertThat(masked).isEqualTo("***.***.247-**");
        assertThat(masked).doesNotContain("529");
    }
}
