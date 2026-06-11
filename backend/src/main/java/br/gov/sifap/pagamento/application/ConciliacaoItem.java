package br.gov.sifap.pagamento.application;

import br.gov.sifap.shared.Money;
import java.util.UUID;

/**
 * A single bank-return line to reconcile (REQ-027/028). Mirrors the legacy CNAB
 * return record: payment id, return code ({@code 00}/{@code 01}/{@code 02}),
 * the value returned by the bank and the bank identifier.
 */
public record ConciliacaoItem(UUID pagamentoId, String codigoRetorno, Money valorBanco, String banco) {}
