package br.gov.sifap.beneficiario.api;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/** Request body to add a dependent (REQ-009/010/011). */
public record DependenteRequest(
        @NotBlank(message = "Nome do dependente é obrigatório") String nome,
        LocalDate dataNascimento,
        @NotBlank(message = "Parentesco é obrigatório") String parentesco) {
}
