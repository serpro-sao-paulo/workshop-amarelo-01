package br.gov.sifap.beneficiario.domain;

import br.gov.sifap.shared.CPF;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Beneficiary aggregate root (REQ-001..011).
 *
 * <p>Owns its dependents (child table, ADR-002). New beneficiaries start with
 * status {@code A} (ATIVO) per REQ-006.
 */
@Entity
@Table(name = "beneficiario")
public class Beneficiario {

    @Id
    private UUID id;

    @Convert(converter = br.gov.sifap.shared.persistence.CpfConverter.class)
    @Column(nullable = false, unique = true)
    private CPF cpf;

    @Column(nullable = false)
    private String nome;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    private String regiao;

    @Convert(converter = SituacaoConverter.class)
    @Column(nullable = false)
    private SituacaoBeneficiario situacao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "beneficiario_id", nullable = false)
    private List<Dependente> dependentes = new ArrayList<>();

    @Column(name = "data_cadastro", nullable = false)
    private Instant dataCadastro;

    @Column(name = "data_atualizacao")
    private Instant dataAtualizacao;

    protected Beneficiario() {
        // JPA
    }

    private Beneficiario(CPF cpf, String nome, LocalDate dataNascimento, String regiao) {
        this.id = UUID.randomUUID();
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.regiao = regiao;
        this.situacao = SituacaoBeneficiario.ATIVO; // REQ-006
        this.dataCadastro = Instant.now();
    }

    /** Creates a new active beneficiary (REQ-006). */
    public static Beneficiario novo(CPF cpf, String nome, LocalDate dataNascimento, String regiao) {
        return new Beneficiario(cpf, nome, dataNascimento, regiao);
    }

    /**
     * Adds a dependent respecting the configured limit (REQ-010).
     *
     * @throws br.gov.sifap.shared.error.ConflictException when the limit is reached
     */
    public void adicionarDependente(Dependente dependente, int limite) {
        if (dependentes.size() >= limite) {
            throw new br.gov.sifap.shared.error.ConflictException("LIMITE DE DEPENDENTES ATINGIDO");
        }
        dependentes.add(dependente);
        this.dataAtualizacao = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public CPF getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getRegiao() {
        return regiao;
    }

    public SituacaoBeneficiario getSituacao() {
        return situacao;
    }

    public List<Dependente> getDependentes() {
        return Collections.unmodifiableList(dependentes);
    }

    public Instant getDataCadastro() {
        return dataCadastro;
    }

    public Instant getDataAtualizacao() {
        return dataAtualizacao;
    }
}
