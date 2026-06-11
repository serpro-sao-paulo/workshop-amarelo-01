package br.gov.sifap.programa.infrastructure;

import br.gov.sifap.programa.domain.ProgramaSocial;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramaSocialRepository extends JpaRepository<ProgramaSocial, UUID> {

    boolean existsByCodigo(String codigo);

    Optional<ProgramaSocial> findByCodigo(String codigo);
}
