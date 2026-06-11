package br.gov.sifap.pagamento.api;

import jakarta.validation.constraints.NotNull;
import java.time.YearMonth;
import java.util.UUID;

/** Request to generate the monthly payroll (REQ-025/026). */
public record GerarFolhaRequest(
        @NotNull YearMonth competencia, @NotNull UUID programaId) {}
