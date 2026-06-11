package br.gov.sifap.beneficiario.api;

import br.gov.sifap.beneficiario.domain.Dependente;
import java.time.LocalDate;
import java.util.UUID;

/** Dependent response. */
public record DependenteResponse(
        UUID id,
        String nome,
        LocalDate dataNascimento,
        String parentesco) {

    public static DependenteResponse from(Dependente d) {
        return new DependenteResponse(
                d.getId(),
                d.getNome(),
                d.getDataNascimento(),
                d.getParentesco().codigo());
    }
}
