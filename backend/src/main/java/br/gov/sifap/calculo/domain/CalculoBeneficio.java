package br.gov.sifap.calculo.domain;

import br.gov.sifap.shared.Money;

/**
 * Stateless benefit calculation engine (ADR-001).
 *
 * <p>⏸️ DEFERRED: the core benefit value formula is a blocker (D-01 / OQ-002,
 * MYS-010). The contract is defined here, but no implementation may compute a
 * value until the team ratifies the formula. See research.md D-01.
 */
public interface CalculoBeneficio {

    /**
     * Computes the gross benefit value for a beneficiary/program.
     *
     * @throws UnsupportedOperationException always, until the formula is ratified
     */
    Money calcularValorBase(CalculoInput input);
}
