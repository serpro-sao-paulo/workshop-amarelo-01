package br.gov.sifap.beneficiario.api;

import br.gov.sifap.beneficiario.domain.Beneficiario;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Beneficiary response with masked CPF (LGPD). */
public record BeneficiarioResponse(
        UUID id,
        String cpf,
        String nome,
        LocalDate dataNascimento,
        String regiao,
        String situacao,
        List<DependenteResponse> dependentes) {

    public static BeneficiarioResponse from(Beneficiario b) {
        return new BeneficiarioResponse(
                b.getId(),
                b.getCpf().masked(),
                b.getNome(),
                b.getDataNascimento(),
                b.getRegiao(),
                b.getSituacao().codigo(),
                b.getDependentes().stream().map(DependenteResponse::from).toList());
    }
}
