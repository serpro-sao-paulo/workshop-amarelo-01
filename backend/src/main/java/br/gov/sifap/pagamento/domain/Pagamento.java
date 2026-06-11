package br.gov.sifap.pagamento.domain;

import br.gov.sifap.shared.CPF;
import br.gov.sifap.shared.Money;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Payment aggregate root (REQ-025..030). Written ONLY by the Pagamentos context
 * (ADR-001). New payments start as {@code G} (GERADO).
 *
 * <p>{@code valorLiquido = valorBase + valorCorrecao - totalDescontos} (truncated,
 * REQ-019). The benefit {@code valorBase} formula is DEFERRED (D-01); this
 * aggregate only persists the value supplied by the (gated) Cálculo engine.
 */
@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    private UUID id;

    @Column(name = "beneficiario_id", nullable = false)
    private UUID beneficiarioId;

    @Column(name = "programa_id", nullable = false)
    private UUID programaId;

    @Convert(converter = br.gov.sifap.shared.persistence.YearMonthConverter.class)
    @Column(nullable = false)
    private YearMonth competencia;

    @Convert(converter = br.gov.sifap.shared.persistence.MoneyConverter.class)
    @Column(name = "valor_base", nullable = false)
    private Money valorBase;

    @Convert(converter = br.gov.sifap.shared.persistence.MoneyConverter.class)
    @Column(name = "valor_correcao", nullable = false)
    private Money valorCorrecao;

    @Convert(converter = br.gov.sifap.shared.persistence.MoneyConverter.class)
    @Column(name = "valor_liquido", nullable = false)
    private Money valorLiquido;

    @Convert(converter = TipoPagamentoConverter.class)
    @Column(name = "tipo_pagamento", nullable = false)
    private TipoPagamento tipoPagamento;

    @Convert(converter = StatusPagamentoConverter.class)
    @Column(nullable = false)
    private StatusPagamento status;

    @Convert(converter = br.gov.sifap.shared.persistence.CpfConverter.class)
    @Column(nullable = false)
    private CPF cpf;

    @Column(nullable = false)
    private boolean divergente;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pagamento_id", nullable = false)
    private List<Desconto> descontos = new ArrayList<>();

    @Column(name = "data_geracao", nullable = false)
    private Instant dataGeracao;

    @Column(name = "data_conciliacao")
    private Instant dataConciliacao;

    private String banco;

    protected Pagamento() {
        // JPA
    }

    private Pagamento(
            UUID beneficiarioId,
            UUID programaId,
            CPF cpf,
            YearMonth competencia,
            Money valorBase,
            Money valorCorrecao,
            Money totalDescontos,
            TipoPagamento tipoPagamento,
            List<Desconto> descontos) {
        this.id = UUID.randomUUID();
        this.beneficiarioId = beneficiarioId;
        this.programaId = programaId;
        this.cpf = cpf;
        this.competencia = competencia;
        this.valorBase = valorBase;
        this.valorCorrecao = valorCorrecao;
        this.tipoPagamento = tipoPagamento;
        this.status = StatusPagamento.GERADO;
        this.divergente = false;
        this.dataGeracao = Instant.now();
        if (descontos != null) {
            this.descontos.addAll(descontos);
        }
        this.valorLiquido = valorBase.add(valorCorrecao).subtract(totalDescontos); // REQ-019
    }

    /** Generates a new payment with status {@code G} (REQ-030). */
    public static Pagamento gerar(
            UUID beneficiarioId,
            UUID programaId,
            CPF cpf,
            YearMonth competencia,
            Money valorBase,
            Money valorCorrecao,
            Money totalDescontos,
            TipoPagamento tipoPagamento,
            List<Desconto> descontos) {
        return new Pagamento(
                beneficiarioId, programaId, cpf, competencia,
                valorBase, valorCorrecao, totalDescontos, tipoPagamento, descontos);
    }

    /** REQ-027: return code {@code 00} → {@code P} (pago), records date and bank. */
    public void conciliarPago(String banco, Instant quando) {
        this.status = StatusPagamento.PAGO;
        this.banco = banco;
        this.dataConciliacao = quando;
    }

    /** REQ-027: return code {@code 01} → {@code D} (devolvido). */
    public void conciliarDevolvido(Instant quando) {
        this.status = StatusPagamento.DEVOLVIDO;
        this.dataConciliacao = quando;
    }

    /** REQ-027: return code {@code 02} → {@code E} (estornado). */
    public void conciliarEstornado(Instant quando) {
        this.status = StatusPagamento.ESTORNADO;
        this.dataConciliacao = quando;
    }

    /** REQ-028: mark as divergent when bank value differs by more than R$ 0,01. */
    public void marcarDivergente() {
        this.divergente = true;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBeneficiarioId() {
        return beneficiarioId;
    }

    public UUID getProgramaId() {
        return programaId;
    }

    public YearMonth getCompetencia() {
        return competencia;
    }

    public Money getValorBase() {
        return valorBase;
    }

    public Money getValorCorrecao() {
        return valorCorrecao;
    }

    public Money getValorLiquido() {
        return valorLiquido;
    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public CPF getCpf() {
        return cpf;
    }

    public boolean isDivergente() {
        return divergente;
    }

    public List<Desconto> getDescontos() {
        return Collections.unmodifiableList(descontos);
    }

    public Instant getDataGeracao() {
        return dataGeracao;
    }

    public Instant getDataConciliacao() {
        return dataConciliacao;
    }

    public String getBanco() {
        return banco;
    }
}
