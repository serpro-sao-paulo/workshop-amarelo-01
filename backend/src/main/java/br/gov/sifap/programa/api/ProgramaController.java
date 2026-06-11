package br.gov.sifap.programa.api;

import br.gov.sifap.programa.application.ProgramaSocialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for the Programas Sociais context (REQ-012, REQ-033). */
@RestController
@RequestMapping("/api/v1/programas")
@Tag(name = "programas", description = "Programas Sociais (REQ-012..017)")
public class ProgramaController {

    private final ProgramaSocialService service;

    public ProgramaController(ProgramaSocialService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastra programa social (REQ-012 — status inicial A)")
    public ResponseEntity<ProgramaResponse> criar(@Valid @RequestBody CriarProgramaRequest request) {
        ProgramaResponse response = ProgramaResponse.from(service.criar(request));
        return ResponseEntity.created(URI.create("/api/v1/programas/" + response.id())).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista programas sociais")
    public ProgramaPageResponse listar(Pageable pageable) {
        Page<ProgramaResponse> page = service.listar(pageable).map(ProgramaResponse::from);
        return new ProgramaPageResponse(
                page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha programa social")
    public ProgramaResponse buscar(@PathVariable UUID id) {
        return ProgramaResponse.from(service.buscarPorId(id));
    }
}
