package br.gov.sifap.programa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Social program aggregate root (REQ-012..017).
 *
 * <p>New programs start with status {@code A} (ATIVO) per REQ-012. Eligibility
 * criteria are embedded (REQ-015/016).
 */
@Entity
@Table(name = "programa_social")
public class ProgramaSocial {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nome;

    @Convert(converter = TipoProgramaConverter.class)
    @Column(nullable = false)
    private TipoPrograma tipo;

    @Convert(converter = StatusProgramaConverter.class)
    @Column(nullable = false)
    private StatusPrograma situacao;

    @Embedded
    private CriteriosElegibilidade criterios;

    @Column(name = "vigencia_inicio")
    private LocalDate vigenciaInicio;

    @Column(name = "vigencia_fim")
    private LocalDate vigenciaFim;

    protected ProgramaSocial() {
        // JPA
    }

    private ProgramaSocial(
            String codigo,
            String nome,
            TipoPrograma tipo,
            CriteriosElegibilidade criterios,
            LocalDate vigenciaInicio,
            LocalDate vigenciaFim) {
        this.id = UUID.randomUUID();
        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
        this.situacao = StatusPrograma.ATIVO; // REQ-012
        this.criterios = criterios;
        this.vigenciaInicio = vigenciaInicio;
        this.vigenciaFim = vigenciaFim;
    }

    /** Creates a new active program (REQ-012). */
    public static ProgramaSocial novo(
            String codigo,
            String nome,
            TipoPrograma tipo,
            CriteriosElegibilidade criterios,
            LocalDate vigenciaInicio,
            LocalDate vigenciaFim) {
        return new ProgramaSocial(codigo, nome, tipo, criterios, vigenciaInicio, vigenciaFim);
    }

    /** REQ-013: eligibility requires the program to be active. */
    public boolean isAtivo() {
        return situacao == StatusPrograma.ATIVO;
    }

    public UUID getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public TipoPrograma getTipo() {
        return tipo;
    }

    public StatusPrograma getSituacao() {
        return situacao;
    }

    public CriteriosElegibilidade getCriterios() {
        return criterios;
    }

    public LocalDate getVigenciaInicio() {
        return vigenciaInicio;
    }

    public LocalDate getVigenciaFim() {
        return vigenciaFim;
    }
}
