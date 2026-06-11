package br.gov.sifap.beneficiario.api;

import br.gov.sifap.beneficiario.application.BeneficiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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

/** REST API for the Cadastro de Beneficiários context (REQ-033). */
@RestController
@RequestMapping("/api/v1/beneficiarios")
@Tag(name = "beneficiarios", description = "Cadastro de Beneficiários (REQ-001..011)")
public class BeneficiarioController {

    private final BeneficiarioService service;

    public BeneficiarioController(BeneficiarioService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastra beneficiário (REQ-001..006)")
    public ResponseEntity<BeneficiarioResponse> criar(@Valid @RequestBody CriarBeneficiarioRequest request) {
        BeneficiarioResponse response = BeneficiarioResponse.from(service.criar(request));
        return ResponseEntity.created(URI.create("/api/v1/beneficiarios/" + response.id())).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista beneficiários (CPF mascarado)")
    public BeneficiarioPageResponse listar(Pageable pageable) {
        Page<BeneficiarioResponse> page = service.listar(pageable).map(BeneficiarioResponse::from);
        return new BeneficiarioPageResponse(
                page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha beneficiário")
    public BeneficiarioResponse buscar(@PathVariable UUID id) {
        return BeneficiarioResponse.from(service.buscarPorId(id));
    }

    @GetMapping("/{id}/dependentes")
    @Operation(summary = "Lista dependentes (REQ-009..011)")
    public List<DependenteResponse> dependentes(@PathVariable UUID id) {
        return service.listarDependentes(id).stream().map(DependenteResponse::from).toList();
    }
}
