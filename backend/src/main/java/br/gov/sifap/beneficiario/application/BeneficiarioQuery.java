package br.gov.sifap.beneficiario.application;

import br.gov.sifap.beneficiario.domain.SituacaoBeneficiario;
import java.util.Optional;
import java.util.UUID;

/**
 * Synchronous query port exposed to other bounded contexts (R-05, ADR-004).
 *
 * <p>Programas/Elegibilidade and Pagamentos use this to read beneficiary state
 * in-process without depending on the cadastro internals.
 */
public interface BeneficiarioQuery {

    Optional<SituacaoBeneficiario> situacaoPorId(UUID beneficiarioId);

    Optional<BeneficiarioElegibilidadeView> dadosElegibilidade(UUID beneficiarioId);

    /** Active beneficiaries ordered ascending by CPF for the monthly payroll (REQ-025/026). */
    java.util.List<BeneficiarioFolhaView> ativosOrdenadosPorCpf();

    boolean existePorId(UUID beneficiarioId);
}
