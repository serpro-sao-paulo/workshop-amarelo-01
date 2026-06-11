package br.gov.sifap.pagamento.application;

import br.gov.sifap.pagamento.domain.Pagamento;
import br.gov.sifap.pagamento.infrastructure.PagamentoRepository;
import br.gov.sifap.shared.error.NotFoundException;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read-only queries over payments (REQ-030). */
@Service
public class PagamentoConsultaService {

    private final PagamentoRepository repository;

    public PagamentoConsultaService(PagamentoRepository repository) {
        this.repository = repository;
    }

    /** REQ-025: payments of a competence ordered ascending by CPF. */
    @Transactional(readOnly = true)
    public List<Pagamento> listarPorCompetencia(YearMonth competencia) {
        return repository.findByCompetenciaOrderByCpfAsc(competencia);
    }

    @Transactional(readOnly = true)
    public Pagamento buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("PAGAMENTO NAO ENCONTRADO"));
    }
}
