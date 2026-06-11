package br.gov.sifap.pagamento.domain;

import java.util.Arrays;

/**
 * Payment type domain.
 *
 * <p>{@code M}=mensal, {@code D}=décimo terceiro (13º). The 13º trigger/formula
 * is DEFERRED (D-02); only the type code is modelled here.
 */
public enum TipoPagamento {

    MENSAL("M", "Mensal"),
    DECIMO_TERCEIRO("D", "Décimo Terceiro");

    private final String codigo;
    private final String rotulo;

    TipoPagamento(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String codigo() {
        return codigo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static TipoPagamento fromCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(t -> t.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("TIPO DE PAGAMENTO INVALIDO"));
    }
}
