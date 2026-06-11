package br.gov.sifap.calculo.application;

import br.gov.sifap.pagamento.domain.Desconto;
import br.gov.sifap.shared.Money;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Applies discount rules (REQ-021/022/023).
 *
 * <p>The ceiling is 30% of the gross value (REQ-021). Judicial discounts are
 * summed without a cap (REQ-022); non-judicial discounts are capped at the
 * ceiling (REQ-023).
 *
 * <p>The legacy applied the cap per periodic-group iteration, making the result
 * order-sensitive (REQ-023 note). This implementation defines a deterministic
 * rule: the non-judicial total is computed first and then capped at the ceiling.
 */
@Service
public class DescontoService {

    private static final int TETO_PERCENTUAL = 30; // REQ-021 (CALCDSCT BR-013)

    public DescontoResultado aplicar(Money valorBruto, List<Desconto> descontos) {
        Money teto = valorBruto.percentage(TETO_PERCENTUAL); // REQ-021

        Money totalJudicial = Money.ZERO;
        Money totalOrdinario = Money.ZERO;
        for (Desconto d : descontos) {
            if (d.isJudicial()) {
                totalJudicial = totalJudicial.add(d.getValor()); // REQ-022 (no cap)
            } else {
                totalOrdinario = totalOrdinario.add(d.getValor());
            }
        }

        boolean tetoAplicado = totalOrdinario.isGreaterThan(teto); // REQ-023
        Money ordinarioAplicado = tetoAplicado ? teto : totalOrdinario;

        Money total = totalJudicial.add(ordinarioAplicado);
        return new DescontoResultado(total, totalJudicial, ordinarioAplicado, teto, tetoAplicado);
    }
}
