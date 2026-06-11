package br.gov.sifap.programa.application;

import java.util.List;

/**
 * Result of an eligibility evaluation (REQ-013..017).
 *
 * <p>{@code elegivel} is true only when no rule produced a reason. {@code motivos}
 * lists the ineligibility reasons in evaluation order.
 */
public record ElegibilidadeResultado(boolean elegivel, List<String> motivos) {

    public static ElegibilidadeResultado de(List<String> motivos) {
        return new ElegibilidadeResultado(motivos.isEmpty(), List.copyOf(motivos));
    }
}
