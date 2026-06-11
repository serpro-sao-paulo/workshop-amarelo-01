package br.gov.sifap.auditoria.application;

import br.gov.sifap.auditoria.domain.EventoAuditoria;
import br.gov.sifap.auditoria.infrastructure.EventoAuditoriaRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read-only queries over the append-only audit trail (REQ-032). */
@Service
public class AuditoriaConsultaService {

    private final EventoAuditoriaRepository repository;

    public AuditoriaConsultaService(EventoAuditoriaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EventoAuditoria> trilha() {
        return repository.findAllByOrderByMomentoDesc();
    }

    @Transactional(readOnly = true)
    public List<EventoAuditoria> trilhaDe(String entidade, UUID entidadeId) {
        return repository.findByEntidadeAndEntidadeIdOrderByMomentoAsc(entidade, entidadeId);
    }
}
