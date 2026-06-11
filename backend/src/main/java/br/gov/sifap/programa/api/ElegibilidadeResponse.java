package br.gov.sifap.programa.api;

import br.gov.sifap.programa.application.ElegibilidadeResultado;
import java.util.List;

/** Eligibility evaluation response (REQ-013..017). */
public record ElegibilidadeResponse(boolean elegivel, List<String> motivos) {

    public static ElegibilidadeResponse from(ElegibilidadeResultado resultado) {
        return new ElegibilidadeResponse(resultado.elegivel(), resultado.motivos());
    }
}
