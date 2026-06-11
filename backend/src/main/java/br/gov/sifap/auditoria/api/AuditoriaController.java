package br.gov.sifap.auditoria.api;

import br.gov.sifap.auditoria.application.AuditoriaConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST API for the append-only audit trail (REQ-032, REQ-033). */
@RestController
@RequestMapping("/api/v1/auditoria")
@Tag(name = "auditoria", description = "Trilha de auditoria append-only (REQ-032)")
public class AuditoriaController {

    private final AuditoriaConsultaService service;

    public AuditoriaController(AuditoriaConsultaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista a trilha de auditoria (mais recentes primeiro)")
    public List<EventoAuditoriaResponse> trilha(
            @RequestParam(required = false) String entidade,
            @RequestParam(required = false) UUID entidadeId) {
        List<EventoAuditoriaResponse> resposta;
        if (entidade != null && entidadeId != null) {
            resposta = service.trilhaDe(entidade, entidadeId).stream()
                    .map(EventoAuditoriaResponse::from)
                    .toList();
        } else {
            resposta = service.trilha().stream()
                    .map(EventoAuditoriaResponse::from)
                    .toList();
        }
        return resposta;
    }
}
