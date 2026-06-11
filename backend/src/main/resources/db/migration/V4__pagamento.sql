-- US3 — Processamento de Pagamentos (REQ-025..029).
-- Pagamentos is the ONLY writer of this aggregate (ADR-001). Descontos are a
-- child table (@OneToMany, ADR-002). The benefit value FORMULA is DEFERRED
-- (D-01); valor_base is persisted as supplied by the (gated) Cálculo engine.

CREATE TABLE pagamento (
    id                UUID         PRIMARY KEY,
    beneficiario_id   UUID         NOT NULL,
    programa_id       UUID         NOT NULL,
    competencia       VARCHAR(7)   NOT NULL,
    valor_base        NUMERIC(15,2) NOT NULL,
    valor_correcao    NUMERIC(15,2) NOT NULL DEFAULT 0,
    valor_liquido     NUMERIC(15,2) NOT NULL,
    tipo_pagamento    VARCHAR(2)   NOT NULL,
    status            VARCHAR(2)   NOT NULL,
    cpf               VARCHAR(11)  NOT NULL,
    divergente        BOOLEAN      NOT NULL DEFAULT FALSE,
    data_geracao      TIMESTAMPTZ  NOT NULL,
    data_conciliacao  TIMESTAMPTZ,
    banco             VARCHAR(10)
);

CREATE INDEX idx_pagamento_competencia_cpf ON pagamento (competencia, cpf);
CREATE INDEX idx_pagamento_beneficiario ON pagamento (beneficiario_id);

CREATE TABLE desconto (
    id            UUID          PRIMARY KEY,
    pagamento_id  UUID          NOT NULL,
    tipo          VARCHAR(2)    NOT NULL,
    valor         NUMERIC(15,2) NOT NULL,
    origem        VARCHAR(50),
    CONSTRAINT fk_desconto_pagamento
        FOREIGN KEY (pagamento_id) REFERENCES pagamento (id) ON DELETE CASCADE
);

CREATE INDEX idx_desconto_pagamento ON desconto (pagamento_id);
