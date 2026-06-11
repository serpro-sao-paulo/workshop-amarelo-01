package br.gov.sifap.auditoria.domain;

import java.util.Arrays;

/**
 * Audit action domain (REQ-032, {@code RELAUDIT.NSN#L151-L170}).
 *
 * <p>{@code IN}=inclusão, {@code AL}=alteração, {@code CO}=conciliação,
 * {@code CN}=consulta, {@code DV}=divergência.
 *
 * <p>NEEDS CLARIFICATION: the legacy hides {@code EX} (exclusão) from the trail
 * — a compliance blocker (MYS-029 / OQ-009 / D-05). The modernized trail MUST
 * surface exclusions; the retention/display policy is DEFERRED, so {@code EX}
 * is intentionally not added to this domain yet.
 */
public enum AcaoAuditoria {

    INCLUSAO("IN", "Inclusão"),
    ALTERACAO("AL", "Alteração"),
    CONCILIACAO("CO", "Conciliação"),
    CONSULTA("CN", "Consulta"),
    DIVERGENCIA("DV", "Divergência");

    private final String codigo;
    private final String rotulo;

    AcaoAuditoria(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String codigo() {
        return codigo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static AcaoAuditoria fromCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(a -> a.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ACAO DE AUDITORIA INVALIDA"));
    }
}
