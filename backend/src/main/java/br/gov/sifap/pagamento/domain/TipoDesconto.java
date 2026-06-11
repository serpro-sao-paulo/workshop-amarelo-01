package br.gov.sifap.pagamento.domain;

import java.util.Arrays;

/**
 * Discount type domain (REQ-021/022, {@code CALCDSCT.NSN}).
 *
 * <p>{@code O}=ordinário (subject to the 30% ceiling), {@code J}=judicial
 * (exceeds the ceiling — confirmed legal exception, REQ-022). Alimony ({@code P})
 * is NOT an exception in the legacy and remains an open question (OQ-005), so it
 * is intentionally not modelled as ceiling-exempt here.
 */
public enum TipoDesconto {

    ORDINARIO("O", "Ordinário"),
    JUDICIAL("J", "Judicial");

    private final String codigo;
    private final String rotulo;

    TipoDesconto(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String codigo() {
        return codigo;
    }

    public String rotulo() {
        return rotulo;
    }

    /** REQ-022: judicial discounts are exempt from the 30% ceiling. */
    public boolean isJudicial() {
        return this == JUDICIAL;
    }

    public static TipoDesconto fromCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(t -> t.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("TIPO DE DESCONTO INVALIDO"));
    }
}
