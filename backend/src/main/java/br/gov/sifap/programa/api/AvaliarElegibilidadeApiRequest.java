package br.gov.sifap.programa.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.UUID;

/** Request to evaluate eligibility (REQ-013..017). */
public record AvaliarElegibilidadeApiRequest(
        @NotNull UUID beneficiarioId,
        @NotBlank String programaCodigo,
        @PositiveOrZero BigDecimal rendaFamiliar) {}
