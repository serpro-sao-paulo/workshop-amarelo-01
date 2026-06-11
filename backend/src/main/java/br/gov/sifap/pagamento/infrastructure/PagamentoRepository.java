package br.gov.sifap.pagamento.infrastructure;

import br.gov.sifap.pagamento.domain.Pagamento;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {

    List<Pagamento> findByCompetenciaOrderByCpfAsc(YearMonth competencia);

    boolean existsByBeneficiarioIdAndCompetencia(UUID beneficiarioId, YearMonth competencia);
}
