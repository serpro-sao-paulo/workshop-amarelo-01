package br.gov.sifap.pagamento.api;

import br.gov.sifap.pagamento.application.ConciliacaoItem;
import br.gov.sifap.pagamento.application.ConciliacaoService;
import br.gov.sifap.pagamento.application.FolhaService;
import br.gov.sifap.pagamento.application.PagamentoConsultaService;
import br.gov.sifap.shared.Money;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST API for the Processamento de Pagamentos context (REQ-033). */
@RestController
@RequestMapping("/api/v1/pagamentos")
@Tag(name = "pagamentos", description = "Processamento de Pagamentos (REQ-025..030)")
public class PagamentoController {

    private final FolhaService folhaService;
    private final ConciliacaoService conciliacaoService;
    private final PagamentoConsultaService consultaService;

    public PagamentoController(
            FolhaService folhaService,
            ConciliacaoService conciliacaoService,
            PagamentoConsultaService consultaService) {
        this.folhaService = folhaService;
        this.conciliacaoService = conciliacaoService;
        this.consultaService = consultaService;
    }

    @PostMapping("/folha")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Gera a folha mensal (REQ-025/026). ⏸️ Valor base DEFERIDO (D-01).")
    public List<PagamentoResponse> gerarFolha(@Valid @RequestBody GerarFolhaRequest request) {
        return folhaService.gerarFolha(request.competencia(), request.programaId()).stream()
                .map(PagamentoResponse::from)
                .toList();
    }

    @PostMapping("/conciliacao")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Concilia o retorno bancário (REQ-027/028/029)")
    public ResponseEntity<Void> conciliar(@Valid @RequestBody ConciliacaoRequest request) {
        List<ConciliacaoItem> itens = request.itens().stream()
                .map(i -> new ConciliacaoItem(
                        i.pagamentoId(), i.codigoRetorno(), Money.of(i.valorBanco()), i.banco()))
                .toList();
        conciliacaoService.conciliar(itens);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Lista pagamentos de uma competência (ordenados por CPF, REQ-025)")
    public List<PagamentoResponse> listar(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth competencia) {
        return consultaService.listarPorCompetencia(competencia).stream()
                .map(PagamentoResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha um pagamento")
    public PagamentoResponse buscar(@PathVariable UUID id) {
        return PagamentoResponse.from(consultaService.buscarPorId(id));
    }
}
