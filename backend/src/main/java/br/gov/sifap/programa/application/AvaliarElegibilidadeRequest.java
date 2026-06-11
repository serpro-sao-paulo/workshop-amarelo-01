package br.gov.sifap.programa.application;

import br.gov.sifap.shared.Money;
import java.util.UUID;

/**
 * Command to evaluate the eligibility of a beneficiary for a program
 * (REQ-013..017). {@code rendaFamiliar} is supplied by the caller because it is
 * not part of the beneficiary aggregate.
 */
public record AvaliarElegibilidadeRequest(
        UUID beneficiarioId, String programaCodigo, Money rendaFamiliar) {}
