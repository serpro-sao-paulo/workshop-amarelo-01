package br.gov.sifap.beneficiario.application;

import br.gov.sifap.beneficiario.domain.SituacaoBeneficiario;
import java.time.LocalDate;

/**
 * Read-only projection of beneficiary state used by other contexts to evaluate
 * eligibility (REQ-014/015). Published as part of the {@link BeneficiarioQuery}
 * port contract.
 */
public record BeneficiarioElegibilidadeView(
        SituacaoBeneficiario situacao, LocalDate dataNascimento) {}
