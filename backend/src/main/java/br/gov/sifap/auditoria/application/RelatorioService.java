package br.gov.sifap.auditoria.application;

import br.gov.sifap.beneficiario.domain.SituacaoBeneficiario;
import br.gov.sifap.pagamento.domain.StatusPagamento;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;

/**
 * Read-model labels for reports (REQ-030/031).
 *
 * <p>Translates payment status (REQ-030) and beneficiary status (REQ-031) to
 * display labels. Unknown codes are flagged with an exception rather than
 * silently defaulting (REQ-030).
 */
@Service
public class RelatorioService {

    /** REQ-030: payment status code → label. */
    public Map<String, String> rotulosStatusPagamento() {
        return Arrays.stream(StatusPagamento.values())
                .collect(LinkedHashMap::new, (m, s) -> m.put(s.codigo(), s.rotulo()), Map::putAll);
    }

    /** REQ-031: beneficiary status code → label. */
    public Map<String, String> rotulosStatusBeneficiario() {
        return Arrays.stream(SituacaoBeneficiario.values())
                .collect(LinkedHashMap::new, (m, s) -> m.put(s.codigo(), s.rotulo()), Map::putAll);
    }

    /** REQ-030: translate a payment status code, flagging unknown values. */
    public String traduzirStatusPagamento(String codigo) {
        return traduzir(codigo, StatusPagamento.values(), StatusPagamento::codigo, StatusPagamento::rotulo);
    }

    /** REQ-031: translate a beneficiary status code. */
    public String traduzirStatusBeneficiario(String codigo) {
        return traduzir(
                codigo, SituacaoBeneficiario.values(),
                SituacaoBeneficiario::codigo, SituacaoBeneficiario::rotulo);
    }

    private <E> String traduzir(
            String codigo, E[] valores, Function<E, String> getCodigo, Function<E, String> getRotulo) {
        return Arrays.stream(valores)
                .filter(v -> getCodigo.apply(v).equals(codigo))
                .map(getRotulo)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("STATUS DESCONHECIDO: " + codigo));
    }
}
