package br.gov.sifap.pagamento.application;

import br.gov.sifap.auditoria.application.AuditLog;
import br.gov.sifap.pagamento.domain.Pagamento;
import br.gov.sifap.pagamento.infrastructure.PagamentoRepository;
import br.gov.sifap.shared.error.NotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bank reconciliation service (REQ-027/028/029).
 *
 * <p>Updates payment status by return code (REQ-027), flags divergences greater
 * than R$ 0,01 (REQ-028) and records audit events via the {@link AuditLog} port
 * — Pagamentos never writes the audit log directly (ADR-003, REQ-029).
 */
@Service
public class ConciliacaoService {

    private static final Logger log = LoggerFactory.getLogger(ConciliacaoService.class);
    private static final BigDecimal TOLERANCIA = new BigDecimal("0.01"); // REQ-028

    private final PagamentoRepository pagamentos;
    private final AuditLog auditLog;

    public ConciliacaoService(PagamentoRepository pagamentos, AuditLog auditLog) {
        this.pagamentos = pagamentos;
        this.auditLog = auditLog;
    }

    @Transactional
    public void conciliar(List<ConciliacaoItem> itens) {
        for (ConciliacaoItem item : itens) {
            conciliarItem(item);
        }
    }

    private void conciliarItem(ConciliacaoItem item) {
        Pagamento pagamento = pagamentos.findById(item.pagamentoId())
                .orElseThrow(() -> new NotFoundException("PAGAMENTO NAO ENCONTRADO"));

        Instant agora = Instant.now();

        // REQ-028: divergence detection (absolute difference > R$ 0,01).
        BigDecimal diferenca = pagamento.getValorLiquido().toBigDecimal()
                .subtract(item.valorBanco().toBigDecimal())
                .abs();
        if (diferenca.compareTo(TOLERANCIA) > 0) {
            pagamento.marcarDivergente();
            auditLog.record("DV", "PAGAMENTO", pagamento.getId(), "Divergência na conciliação"); // REQ-029
        }

        // REQ-027: status by bank return code.
        switch (item.codigoRetorno()) {
            case "00" -> pagamento.conciliarPago(item.banco(), agora);
            case "01" -> pagamento.conciliarDevolvido(agora);
            case "02" -> pagamento.conciliarEstornado(agora);
            default -> {
                // REQ-027 note: unknown return codes are logged, not applied.
                log.warn("Código de retorno bancário desconhecido: {}", item.codigoRetorno());
                pagamentos.save(pagamento);
                return;
            }
        }

        auditLog.record("CO", "PAGAMENTO", pagamento.getId(), "Pagamento conciliado"); // REQ-029
        pagamentos.save(pagamento);
    }
}
