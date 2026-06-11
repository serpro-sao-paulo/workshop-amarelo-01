package br.gov.sifap.beneficiario.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Dependent of a beneficiary (REQ-009/010/011).
 *
 * <p>Mapped as a child table of {@code beneficiario} via {@code @OneToMany}
 * (ADR-002), replacing the legacy Adabas periodic group.
 */
@Entity
@Table(name = "dependente")
public class Dependente {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Convert(converter = ParentescoConverter.class)
    @Column(nullable = false)
    private Parentesco parentesco;

    protected Dependente() {
        // JPA
    }

    public Dependente(String nome, LocalDate dataNascimento, Parentesco parentesco) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.parentesco = parentesco;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }
}
