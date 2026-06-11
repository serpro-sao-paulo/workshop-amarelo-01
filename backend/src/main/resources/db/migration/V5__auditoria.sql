-- US4 — Relatórios e Auditoria (REQ-032). Append-only audit trail.
-- Written ONLY through the AuditLog.record() port (ADR-003). No UPDATE/DELETE.

CREATE TABLE evento_auditoria (
    id          UUID         PRIMARY KEY,
    acao        VARCHAR(2)   NOT NULL,
    entidade    VARCHAR(50)  NOT NULL,
    entidade_id UUID,
    usuario     VARCHAR(100),
    detalhe     VARCHAR(500),
    momento     TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_evento_auditoria_entidade ON evento_auditoria (entidade, entidade_id);
CREATE INDEX idx_evento_auditoria_momento ON evento_auditoria (momento);
