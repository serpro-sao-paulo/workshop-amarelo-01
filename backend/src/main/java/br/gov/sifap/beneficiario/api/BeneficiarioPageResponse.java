package br.gov.sifap.beneficiario.api;

import java.util.List;

/** Paginated beneficiary listing matching the {@code BeneficiarioPage} schema. */
public record BeneficiarioPageResponse(
        List<BeneficiarioResponse> content,
        int page,
        int size,
        long totalElements) {
}
