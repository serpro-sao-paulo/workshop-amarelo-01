package br.gov.sifap.programa.application;

import br.gov.sifap.beneficiario.application.BeneficiarioElegibilidadeView;
import br.gov.sifap.beneficiario.application.BeneficiarioQuery;
import br.gov.sifap.beneficiario.domain.SituacaoBeneficiario;
import br.gov.sifap.programa.domain.CriteriosElegibilidade;
import br.gov.sifap.programa.domain.ProgramaSocial;
import br.gov.sifap.programa.infrastructure.ProgramaSocialRepository;
import br.gov.sifap.shared.Money;
import br.gov.sifap.shared.error.NotFoundException;
import br.gov.sifap.shared.error.ValidationException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Synchronous eligibility port (R-05) consumed by other contexts.
 *
 * <p>Implements REQ-013 (active program — hard reject), REQ-014 (beneficiary
 * status), REQ-015 (age bounds), REQ-016 (income ceiling) and REQ-017
 * (type-specific age rules for {@code P}/{@code T}).
 */
@Service
public class ElegibilidadeService {

    private static final int IDADE_MINIMA_PREVIDENCIARIO = 60; // REQ-017 (VALELEG)
    private static final int IDADE_MINIMA_TRABALHO = 16;       // REQ-017 (VALELEG)
    private static final int IDADE_MAXIMA_TRABALHO = 65;       // REQ-017 (VALELEG)

    private final ProgramaSocialRepository programas;
    private final BeneficiarioQuery beneficiarios;

    public ElegibilidadeService(
            ProgramaSocialRepository programas, BeneficiarioQuery beneficiarios) {
        this.programas = programas;
        this.beneficiarios = beneficiarios;
    }

    @Transactional(readOnly = true)
    public ElegibilidadeResultado avaliar(AvaliarElegibilidadeRequest request) {
        ProgramaSocial programa = programas.findByCodigo(request.programaCodigo())
                .orElseThrow(() -> new NotFoundException("PROGRAMA NAO ENCONTRADO"));

        if (!programa.isAtivo()) { // REQ-013
            throw new ValidationException("PROGRAMA INATIVO");
        }

        BeneficiarioElegibilidadeView dados = beneficiarios
                .dadosElegibilidade(request.beneficiarioId())
                .orElseThrow(() -> new NotFoundException("BENEFICIARIO NAO ENCONTRADO"));

        int idade = Period.between(dados.dataNascimento(), LocalDate.now()).getYears();
        List<String> motivos = new ArrayList<>();

        avaliarStatus(dados.situacao(), motivos);                       // REQ-014
        avaliarFaixaEtaria(programa.getCriterios(), idade, motivos);    // REQ-015
        avaliarRenda(programa.getCriterios(), request.rendaFamiliar(), motivos); // REQ-016
        avaliarTipo(programa, idade, motivos);                          // REQ-017

        return ElegibilidadeResultado.de(motivos);
    }

    private void avaliarStatus(SituacaoBeneficiario situacao, List<String> motivos) {
        if (situacao == SituacaoBeneficiario.ATIVO) {
            return;
        }
        String motivo = switch (situacao) {
            case SUSPENSO -> "SUSPENSO";
            case CANCELADO, DESLIGADO -> "CANCELADO/DESLIGADO";
            case INATIVO -> "INATIVO";
            case ATIVO -> ""; // unreachable
        };
        motivos.add(motivo);
    }

    private void avaliarFaixaEtaria(
            CriteriosElegibilidade criterios, int idade, List<String> motivos) {
        if (criterios.temIdadeMinima() && idade < criterios.getIdadeMinima()) {
            motivos.add("IDADE INFERIOR A MINIMA");
        }
        if (criterios.temIdadeMaxima() && idade > criterios.getIdadeMaxima()) {
            motivos.add("IDADE SUPERIOR A MAXIMA");
        }
    }

    private void avaliarRenda(
            CriteriosElegibilidade criterios, Money rendaFamiliar, List<String> motivos) {
        if (criterios.temRendaMaxima()
                && rendaFamiliar != null
                && rendaFamiliar.isGreaterThan(criterios.getRendaMaxima())) {
            motivos.add("RENDA FAMILIAR EXCEDE O TETO");
        }
    }

    private void avaliarTipo(ProgramaSocial programa, int idade, List<String> motivos) {
        switch (programa.getTipo()) {
            case PREVIDENCIARIO -> {
                if (idade < IDADE_MINIMA_PREVIDENCIARIO) {
                    motivos.add("IDADE MINIMA 60 PARA PREVIDENCIARIO");
                }
            }
            case TRABALHO -> {
                if (idade < IDADE_MINIMA_TRABALHO || idade > IDADE_MAXIMA_TRABALHO) {
                    motivos.add("FORA DA FAIXA 16-65 PARA TRABALHO");
                }
            }
            case ASSISTENCIAL -> {
                // NEEDS CLARIFICATION: the assistencial type-specific rule (income 600 +
                // full documentation) relies on undocumented magic numbers and is DEFERRED
                // (OQ on eligibility magic numbers). Only REQ-015/016 general criteria apply.
            }
        }
    }
}
