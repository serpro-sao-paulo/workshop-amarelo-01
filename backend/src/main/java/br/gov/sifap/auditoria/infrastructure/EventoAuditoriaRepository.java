package br.gov.sifap.auditoria.infrastructure;

import br.gov.sifap.auditoria.domain.EventoAuditoria;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoAuditoriaRepository extends JpaRepository<EventoAuditoria, UUID> {

    List<EventoAuditoria> findByEntidadeAndEntidadeIdOrderByMomentoAsc(
            String entidade, UUID entidadeId);

    List<EventoAuditoria> findAllByOrderByMomentoDesc();
}
