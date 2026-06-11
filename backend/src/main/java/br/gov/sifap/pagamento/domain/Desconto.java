package br.gov.sifap.pagamento.domain;

import br.gov.sifap.shared.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Discount child entity (ADR-002, REQ-021/022). Owned by {@link Pagamento}.
 */
@Entity
@Table(name = "desconto")
public class Desconto {

    @Id
    private UUID id;

    @Convert(converter = TipoDescontoConverter.class)
    @Column(nullable = false)
    private TipoDesconto tipo;

    @Convert(converter = br.gov.sifap.shared.persistence.MoneyConverter.class)
    @Column(nullable = false)
    private Money valor;

    private String origem;

    protected Desconto() {
        // JPA
    }

    public Desconto(TipoDesconto tipo, Money valor, String origem) {
        this.id = UUID.randomUUID();
        this.tipo = tipo;
        this.valor = valor;
        this.origem = origem;
    }

    public UUID getId() {
        return id;
    }

    public TipoDesconto getTipo() {
        return tipo;
    }

    public Money getValor() {
        return valor;
    }

    public String getOrigem() {
        return origem;
    }

    public boolean isJudicial() {
        return tipo.isJudicial();
    }
}
