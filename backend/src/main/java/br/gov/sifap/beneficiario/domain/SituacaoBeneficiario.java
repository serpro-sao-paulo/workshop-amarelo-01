package br.gov.sifap.beneficiario.domain;

import java.util.Arrays;

/**
 * Closed domain of beneficiary status (REQ-008, {@code VALBENEF.NSN#L176-L182}).
 *
 * <p>Codes resolved by evidence (MYS-004): {@code A}=ativo, {@code S}=suspenso,
 * {@code C}=cancelado, {@code I}=inativo, {@code D}=desligado. Labels are used
 * by reports (REQ-031).
 */
public enum SituacaoBeneficiario {

    ATIVO("A", "Ativo"),
    SUSPENSO("S", "Suspenso"),
    CANCELADO("C", "Cancelado"),
    INATIVO("I", "Inativo"),
    DESLIGADO("D", "Desligado");

    private final String codigo;
    private final String rotulo;

    SituacaoBeneficiario(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String codigo() {
        return codigo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static SituacaoBeneficiario fromCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(s -> s.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("STATUS INVALIDO"));
    }
}
