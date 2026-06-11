package br.gov.sifap.beneficiario.domain;

import java.util.Arrays;

/**
 * Closed domain of dependent kinship (REQ-011, {@code CADDEPEND.NSN#L83-L87}).
 *
 * <p>The legacy code accepts four values: {@code FI}=filho, {@code CO}=cônjuge,
 * {@code IR}=irmão, {@code OU}=outro. (The manual cites only three — discrepancy
 * pending ratification.)
 */
public enum Parentesco {

    FILHO("FI", "Filho"),
    CONJUGE("CO", "Cônjuge"),
    IRMAO("IR", "Irmão"),
    OUTRO("OU", "Outro");

    private final String codigo;
    private final String rotulo;

    Parentesco(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String codigo() {
        return codigo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static Parentesco fromCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(p -> p.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("PARENTESCO INVALIDO"));
    }
}
