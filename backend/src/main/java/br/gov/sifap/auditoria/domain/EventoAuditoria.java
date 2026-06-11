package br.gov.sifap.auditoria.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Append-only audit event (REQ-032, ADR-003). No update/delete — written only
 * via the {@code AuditLog.record()} port.
 */
@Entity
@Table(name = "evento_auditoria")
public class EventoAuditoria {

    @Id
    private UUID id;

    @Convert(converter = AcaoAuditoriaConverter.class)
    @Column(nullable = false, length = 2)
    private AcaoAuditoria acao;

    @Column(nullable = false, length = 50)
    private String entidade;

    @Column(name = "entidade_id")
    private UUID entidadeId;

    @Column(length = 100)
    private String usuario;

    @Column(length = 500)
    private String detalhe;

    @Column(nullable = false)
    private Instant momento;

    protected EventoAuditoria() {
        // JPA
    }

    private EventoAuditoria(
            AcaoAuditoria acao, String entidade, UUID entidadeId, String usuario, String detalhe) {
        this.id = UUID.randomUUID();
        this.acao = acao;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.usuario = usuario;
        this.detalhe = detalhe;
        this.momento = Instant.now();
    }

    public static EventoAuditoria registrar(
            AcaoAuditoria acao, String entidade, UUID entidadeId, String usuario, String detalhe) {
        return new EventoAuditoria(acao, entidade, entidadeId, usuario, detalhe);
    }

    public UUID getId() {
        return id;
    }

    public AcaoAuditoria getAcao() {
        return acao;
    }

    public String getEntidade() {
        return entidade;
    }

    public UUID getEntidadeId() {
        return entidadeId;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public Instant getMomento() {
        return momento;
    }
}
