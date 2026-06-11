package br.gov.sifap.programa.domain;

import java.util.Arrays;

/**
 * Program status domain (REQ-012/013, {@code CADPROG.NSN#L97}).
 *
 * <p>{@code A}=ativo, {@code I}=inativo. New programs start as {@code A}
 * (REQ-012); eligibility requires status {@code A} (REQ-013).
 */
public enum StatusPrograma {

    ATIVO("A", "Ativo"),
    INATIVO("I", "Inativo");

    private final String codigo;
    private final String rotulo;

    StatusPrograma(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String codigo() {
        return codigo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static StatusPrograma fromCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(s -> s.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("STATUS DE PROGRAMA INVALIDO"));
    }
}
