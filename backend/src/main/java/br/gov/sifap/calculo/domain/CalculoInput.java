package br.gov.sifap.calculo.domain;

import java.time.YearMonth;
import java.util.UUID;

/**
 * Input for a stateless benefit calculation (ADR-001). Fields are intentionally
 * minimal; the full set depends on the DEFERRED formula (D-01).
 */
public record CalculoInput(UUID beneficiarioId, UUID programaId, YearMonth competencia) {}
