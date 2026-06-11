package br.gov.sifap.programa.api;

import br.gov.sifap.programa.domain.ProgramaSocial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Program representation returned by the API (REQ-012). */
public record ProgramaResponse(
        UUID id,
        String codigo,
        String nome,
        String tipo,
        String tipoRotulo,
        String situacao,
        String situacaoRotulo,
        int idadeMinima,
        int idadeMaxima,
        BigDecimal rendaMaxima,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFim) {

    public static ProgramaResponse from(ProgramaSocial p) {
        return new ProgramaResponse(
                p.getId(),
                p.getCodigo(),
                p.getNome(),
                p.getTipo().codigo(),
                p.getTipo().rotulo(),
                p.getSituacao().codigo(),
                p.getSituacao().rotulo(),
                p.getCriterios().getIdadeMinima(),
                p.getCriterios().getIdadeMaxima(),
                p.getCriterios().getRendaMaxima() == null
                        ? null
                        : p.getCriterios().getRendaMaxima().toBigDecimal(),
                p.getVigenciaInicio(),
                p.getVigenciaFim());
    }
}
