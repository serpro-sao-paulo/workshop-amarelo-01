package br.gov.sifap.calculo.application;

import br.gov.sifap.calculo.domain.CalculoBeneficio;
import br.gov.sifap.calculo.domain.CalculoInput;
import br.gov.sifap.shared.Money;
import org.springframework.stereotype.Component;

/**
 * ⏸️ DEFERRED placeholder for the benefit value formula (D-01 / OQ-002).
 *
 * <p>NEEDS CLARIFICATION: the nuclear benefit formula, the 13º (D-02) and the
 * source of truth for the IPCA table (REQ-020, MYS-014) are blockers. This bean
 * keeps the wiring valid but MUST NOT return a computed value until the team
 * ratifies the formula.
 */
@Component
public class CalculoBeneficioStub implements CalculoBeneficio {

    @Override
    public Money calcularValorBase(CalculoInput input) {
        throw new UnsupportedOperationException(
                "NEEDS CLARIFICATION: fórmula do valor base DEFERIDA (D-01/OQ-002)");
    }
}
