package br.gov.sifap.calculo.application;

import br.gov.sifap.shared.Money;

/**
 * Result of applying discount rules (REQ-021/022/023).
 *
 * @param total           total discounts applied (judicial + capped non-judicial)
 * @param totalJudicial   judicial discounts (uncapped, REQ-022)
 * @param totalOrdinario  non-judicial discounts after the 30% cap (REQ-023)
 * @param teto            the 30% ceiling of the gross value (REQ-021)
 * @param tetoAplicado    whether the non-judicial total was capped
 */
public record DescontoResultado(
        Money total, Money totalJudicial, Money totalOrdinario, Money teto, boolean tetoAplicado) {}
