package br.gov.sifap.pagamento.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Request to reconcile a batch of bank-return lines (REQ-027/028). */
public record ConciliacaoRequest(@NotEmpty List<@Valid Item> itens) {

    public record Item(
            @NotNull UUID pagamentoId,
            @NotBlank String codigoRetorno,
            @NotNull BigDecimal valorBanco,
            String banco) {}
}
