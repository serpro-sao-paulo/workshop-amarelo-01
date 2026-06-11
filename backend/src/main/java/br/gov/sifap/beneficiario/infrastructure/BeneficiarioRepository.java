package br.gov.sifap.beneficiario.infrastructure;

import br.gov.sifap.beneficiario.domain.Beneficiario;
import br.gov.sifap.beneficiario.domain.SituacaoBeneficiario;
import br.gov.sifap.shared.CPF;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the beneficiary aggregate. */
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, UUID> {

    boolean existsByCpf(CPF cpf);

    Optional<Beneficiario> findByCpf(CPF cpf);

    List<Beneficiario> findBySituacaoOrderByCpfAsc(SituacaoBeneficiario situacao);
}
