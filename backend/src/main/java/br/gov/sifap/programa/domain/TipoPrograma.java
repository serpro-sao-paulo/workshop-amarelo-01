package br.gov.sifap.programa.domain;

import java.util.Arrays;

/**
 * Program type domain (REQ-017, {@code VALELEG.NSN#L168-L201}).
 *
 * <p>{@code P}=previdenciário, {@code T}=trabalho, {@code A}=assistencial. The
 * type-specific eligibility rule for {@code A} carries undocumented magic
 * numbers and is DEFERRED (OQ on eligibility magic numbers); only P and T have
 * confirmed age rules.
 */
public enum TipoPrograma {

    PREVIDENCIARIO("P", "Previdenciário"),
    TRABALHO("T", "Trabalho"),
    ASSISTENCIAL("A", "Assistencial");

    private final String codigo;
    private final String rotulo;

    TipoPrograma(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String codigo() {
        return codigo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static TipoPrograma fromCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(t -> t.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("TIPO DE PROGRAMA INVALIDO"));
    }
}
