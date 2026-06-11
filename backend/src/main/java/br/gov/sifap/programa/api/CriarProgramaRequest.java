package br.gov.sifap.programa.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request to create a social program (REQ-012). Age bounds default to zero
 * ("no bound") and {@code rendaMaxima} is optional ("no ceiling").
 */
public record CriarProgramaRequest(
        @NotBlank String codigo,
        @NotBlank String nome,
        @NotBlank String tipo,
        @PositiveOrZero Integer idadeMinima,
        @PositiveOrZero Integer idadeMaxima,
        @PositiveOrZero BigDecimal rendaMaxima,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFim) {}
