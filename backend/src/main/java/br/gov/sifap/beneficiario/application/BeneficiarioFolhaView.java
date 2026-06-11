package br.gov.sifap.beneficiario.application;

import br.gov.sifap.shared.CPF;
import java.util.UUID;

/**
 * Read-only projection used by Pagamentos to build the monthly payroll ordered
 * by CPF (REQ-025/026). Published as part of the {@link BeneficiarioQuery} port.
 */
public record BeneficiarioFolhaView(UUID id, CPF cpf) {}
