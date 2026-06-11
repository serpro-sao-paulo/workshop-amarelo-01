package br.gov.sifap.calculo.application;

import br.gov.sifap.shared.Money;
import java.time.YearMonth;
import org.springframework.stereotype.Service;

/**
 * ⏸️ DEFERRED — monetary correction (REQ-020 / MYS-014) and Christmas bonus /
 * 13º (REQ-024 / D-02).
 *
 * <p>NEEDS CLARIFICATION: the IPCA table coverage (legacy only has 2010–2012 and
 * silently falls back to index 1.0) and the 13º formula are operational
 * blockers. The accumulation mechanics (REQ-020) and the 15% bonus rule
 * (REQ-024) are documented in the spec, but the data source and magic numbers
 * are not ratified. These methods MUST NOT be wired into payment generation
 * until the team resolves D-02 / OQ on IPCA.
 */
@Service
public class CorrecaoMonetariaService {

    /** ⏸️ DEFERRED (REQ-020): correct a gross value by the accumulated IPCA index. */
    public Money corrigir(Money valorOriginal, YearMonth inicio, YearMonth fim) {
        throw new UnsupportedOperationException(
                "NEEDS CLARIFICATION: correção IPCA DEFERIDA (REQ-020/MYS-014)");
    }

    /** ⏸️ DEFERRED (REQ-024): 15% Christmas bonus for assistencial programs in December. */
    public Money abonoNatalino(Money beneficioMensal, YearMonth competencia, String tipoPrograma) {
        throw new UnsupportedOperationException(
                "NEEDS CLARIFICATION: abono natalino/13º DEFERIDO (REQ-024/D-02)");
    }
}
