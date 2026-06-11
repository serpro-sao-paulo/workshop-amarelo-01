package br.gov.sifap.pagamento.domain;

import java.util.Arrays;

/**
 * Payment status domain and labels (REQ-030, {@code BATCHREL.NSN#L176-L195},
 * {@code RELPGT.NSN#L143-L156}).
 *
 * <p>{@code G}=gerado, {@code P}=pago, {@code C}=cancelado, {@code D}=devolvido,
 * {@code E}=estornado. Unknown codes must be flagged, not silenced (REQ-030).
 */
public enum StatusPagamento {

    GERADO("G", "Gerado"),
    PAGO("P", "Pago"),
    CANCELADO("C", "Cancelado"),
    DEVOLVIDO("D", "Devolvido"),
    ESTORNADO("E", "Estornado");

    private final String codigo;
    private final String rotulo;

    StatusPagamento(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String codigo() {
        return codigo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static StatusPagamento fromCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(s -> s.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("STATUS DE PAGAMENTO DESCONHECIDO"));
    }
}
