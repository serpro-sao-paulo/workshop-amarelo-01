package br.gov.sifap.programa.application;

import br.gov.sifap.programa.api.CriarProgramaRequest;
import br.gov.sifap.programa.domain.CriteriosElegibilidade;
import br.gov.sifap.programa.domain.ProgramaSocial;
import br.gov.sifap.programa.domain.TipoPrograma;
import br.gov.sifap.programa.infrastructure.ProgramaSocialRepository;
import br.gov.sifap.shared.Money;
import br.gov.sifap.shared.error.ConflictException;
import br.gov.sifap.shared.error.NotFoundException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for the Programas Sociais context (REQ-012).
 *
 * <p>{@code @Transactional} lives only here (never on repositories).
 */
@Service
public class ProgramaSocialService {

    private final ProgramaSocialRepository repository;

    public ProgramaSocialService(ProgramaSocialRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProgramaSocial criar(CriarProgramaRequest request) {
        if (repository.existsByCodigo(request.codigo())) {
            throw new ConflictException("PROGRAMA JA CADASTRADO");
        }

        Money rendaMaxima = request.rendaMaxima() == null ? null : Money.of(request.rendaMaxima());

        CriteriosElegibilidade criterios = new CriteriosElegibilidade(
                request.idadeMinima() == null ? 0 : request.idadeMinima(),
                request.idadeMaxima() == null ? 0 : request.idadeMaxima(),
                rendaMaxima);

        ProgramaSocial programa = ProgramaSocial.novo( // REQ-012 sets status A
                request.codigo(),
                request.nome(),
                TipoPrograma.fromCodigo(request.tipo()),
                criterios,
                request.vigenciaInicio(),
                request.vigenciaFim());

        return repository.save(programa);
    }

    @Transactional(readOnly = true)
    public ProgramaSocial buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("PROGRAMA NAO ENCONTRADO"));
    }

    @Transactional(readOnly = true)
    public Page<ProgramaSocial> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
