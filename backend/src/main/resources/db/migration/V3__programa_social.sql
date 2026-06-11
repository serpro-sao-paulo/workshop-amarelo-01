-- US2 — Programas Sociais e Elegibilidade (REQ-012..017).
-- Eligibility criteria are embedded columns on the programa_social table.

CREATE TABLE programa_social (
    id              UUID         PRIMARY KEY,
    codigo          VARCHAR(20)  NOT NULL,
    nome            VARCHAR(150) NOT NULL,
    tipo            VARCHAR(2)   NOT NULL,
    situacao        VARCHAR(2)   NOT NULL,
    idade_minima    INTEGER      NOT NULL DEFAULT 0,
    idade_maxima    INTEGER      NOT NULL DEFAULT 0,
    renda_maxima    NUMERIC(15,2),
    vigencia_inicio DATE,
    vigencia_fim    DATE,
    CONSTRAINT uq_programa_social_codigo UNIQUE (codigo)
);
