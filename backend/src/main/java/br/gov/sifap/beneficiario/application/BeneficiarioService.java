package br.gov.sifap.beneficiario.application;

import br.gov.sifap.beneficiario.api.CriarBeneficiarioRequest;
import br.gov.sifap.beneficiario.api.DependenteRequest;
import br.gov.sifap.beneficiario.domain.Beneficiario;
import br.gov.sifap.beneficiario.domain.Dependente;
import br.gov.sifap.beneficiario.domain.Parentesco;
import br.gov.sifap.beneficiario.domain.SituacaoBeneficiario;
import br.gov.sifap.beneficiario.infrastructure.BeneficiarioRepository;
import br.gov.sifap.shared.CPF;
import br.gov.sifap.shared.error.ConflictException;
import br.gov.sifap.shared.error.NotFoundException;
import br.gov.sifap.shared.error.ValidationException;
import br.gov.sifap.shared.events.DomainEventPublisher;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for the Cadastro de Beneficiários context.
 *
 * <p>Also implements the {@link BeneficiarioQuery} port consumed by other
 * contexts. {@code @Transactional} lives only here (never on repositories).
 */
@Service
public class BeneficiarioService implements BeneficiarioQuery {

    private final BeneficiarioRepository repository;
    private final DomainEventPublisher events;
    private final int limiteDependentes;

    public BeneficiarioService(
            BeneficiarioRepository repository,
            DomainEventPublisher events,
            @Value("${sifap.beneficiario.limite-dependentes:5}") int limiteDependentes) {
        this.repository = repository;
        this.events = events;
        this.limiteDependentes = limiteDependentes;
    }

    @Transactional
    public Beneficiario criar(CriarBeneficiarioRequest request) {
        validarNomeCompleto(request.nome()); // REQ-005
        CPF cpf = CPF.of(request.cpf());      // REQ-001/002/003

        if (repository.existsByCpf(cpf)) {    // REQ-007
            throw new ConflictException("BENEFICIARIO JA CADASTRADO");
        }

        Beneficiario beneficiario = Beneficiario.novo(
                cpf, request.nome(), request.dataNascimento(), request.regiao()); // REQ-006

        if (request.dependentes() != null) {
            for (DependenteRequest dep : request.dependentes()) {
                beneficiario.adicionarDependente(toDependente(dep), limiteDependentes); // REQ-010/011
            }
        }

        Beneficiario salvo = repository.save(beneficiario);
        events.publish(BeneficiarioCadastradoEvent.of(salvo.getId()));
        return salvo;
    }

    @Transactional(readOnly = true)
    public Beneficiario buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("BENEFICIARIO NAO ENCONTRADO"));
    }

    @Transactional(readOnly = true)
    public Page<Beneficiario> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Dependente> listarDependentes(UUID beneficiarioId) {
        return buscarPorId(beneficiarioId).getDependentes();
    }

    private Dependente toDependente(DependenteRequest dep) {
        Parentesco parentesco = Parentesco.fromCodigo(dep.parentesco()); // REQ-011
        return new Dependente(dep.nome(), dep.dataNascimento(), parentesco);
    }

    private void validarNomeCompleto(String nome) {
        if (nome == null || nome.trim().split("\\s+").length < 2) {
            throw new ValidationException("NOME INCOMPLETO"); // REQ-005
        }
    }

    // --- BeneficiarioQuery port ---

    @Override
    @Transactional(readOnly = true)
    public Optional<SituacaoBeneficiario> situacaoPorId(UUID beneficiarioId) {
        return repository.findById(beneficiarioId).map(Beneficiario::getSituacao);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorId(UUID beneficiarioId) {
        return repository.existsById(beneficiarioId);
    }
}
