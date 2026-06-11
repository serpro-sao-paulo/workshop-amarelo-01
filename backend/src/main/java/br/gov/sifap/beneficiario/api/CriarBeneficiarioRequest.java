package br.gov.sifap.beneficiario.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.List;

/** Request body to create a beneficiary (REQ-001/003/004/005). */
public record CriarBeneficiarioRequest(
        @NotBlank(message = "CPF é obrigatório") String cpf,
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser no passado") LocalDate dataNascimento,
        String regiao,
        @Valid List<DependenteRequest> dependentes) {
}
