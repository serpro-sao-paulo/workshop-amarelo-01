package br.gov.sifap.pagamento.api;

import br.gov.sifap.pagamento.domain.Pagamento;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Payment representation returned by the API (REQ-030). CPF is masked (LGPD). */
public record PagamentoResponse(
        UUID id,
        UUID beneficiarioId,
        UUID programaId,
        String cpf,
        String competencia,
        BigDecimal valorBase,
        BigDecimal valorCorrecao,
        BigDecimal valorLiquido,
        String tipoPagamento,
        String status,
        String statusRotulo,
        boolean divergente,
        Instant dataGeracao,
        Instant dataConciliacao) {

    public static PagamentoResponse from(Pagamento p) {
        return new PagamentoResponse(
                p.getId(),
                p.getBeneficiarioId(),
                p.getProgramaId(),
                p.getCpf().masked(),
                p.getCompetencia().toString(),
                p.getValorBase().toBigDecimal(),
                p.getValorCorrecao().toBigDecimal(),
                p.getValorLiquido().toBigDecimal(),
                p.getTipoPagamento().codigo(),
                p.getStatus().codigo(),
                p.getStatus().rotulo(),
                p.isDivergente(),
                p.getDataGeracao(),
                p.getDataConciliacao());
    }
}
