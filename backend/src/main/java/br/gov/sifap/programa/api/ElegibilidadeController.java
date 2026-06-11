package br.gov.sifap.programa.api;

import br.gov.sifap.programa.application.AvaliarElegibilidadeRequest;
import br.gov.sifap.programa.application.ElegibilidadeService;
import br.gov.sifap.shared.Money;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for synchronous eligibility evaluation (REQ-013..017, REQ-033). */
@RestController
@RequestMapping("/api/v1/elegibilidade")
@Tag(name = "elegibilidade", description = "Avaliação de elegibilidade (REQ-013..017)")
public class ElegibilidadeController {

    private final ElegibilidadeService service;

    public ElegibilidadeController(ElegibilidadeService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Avalia elegibilidade de um beneficiário para um programa")
    public ElegibilidadeResponse avaliar(@Valid @RequestBody AvaliarElegibilidadeApiRequest request) {
        Money renda = request.rendaFamiliar() == null ? null : Money.of(request.rendaFamiliar());
        AvaliarElegibilidadeRequest command = new AvaliarElegibilidadeRequest(
                request.beneficiarioId(), request.programaCodigo(), renda);
        return ElegibilidadeResponse.from(service.avaliar(command));
    }
}
