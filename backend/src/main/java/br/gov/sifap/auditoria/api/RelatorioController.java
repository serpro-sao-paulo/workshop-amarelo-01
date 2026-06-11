package br.gov.sifap.auditoria.api;

import br.gov.sifap.auditoria.application.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for report read-models / status labels (REQ-030/031, REQ-033). */
@RestController
@RequestMapping("/api/v1/relatorios")
@Tag(name = "relatorios", description = "Relatórios e rótulos de status (REQ-030/031)")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping("/status-pagamento")
    @Operation(summary = "Rótulos do domínio de status de pagamento (REQ-030)")
    public Map<String, String> statusPagamento() {
        return service.rotulosStatusPagamento();
    }

    @GetMapping("/status-beneficiario")
    @Operation(summary = "Rótulos do domínio de status de beneficiário (REQ-031)")
    public Map<String, String> statusBeneficiario() {
        return service.rotulosStatusBeneficiario();
    }
}
