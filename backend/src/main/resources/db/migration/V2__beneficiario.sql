-- US1 — Cadastro de Beneficiários (REQ-001..011).
-- Dependentes are mapped as a child table (@OneToMany) per ADR-002.

CREATE TABLE beneficiario (
    id               UUID         PRIMARY KEY,
    cpf              VARCHAR(11)  NOT NULL,
    nome             VARCHAR(150) NOT NULL,
    data_nascimento  DATE         NOT NULL,
    regiao           VARCHAR(10),
    situacao         VARCHAR(20)  NOT NULL,
    data_cadastro    TIMESTAMPTZ  NOT NULL,
    data_atualizacao TIMESTAMPTZ,
    CONSTRAINT uq_beneficiario_cpf UNIQUE (cpf)
);

CREATE TABLE dependente (
    id               UUID         PRIMARY KEY,
    beneficiario_id  UUID         NOT NULL,
    nome             VARCHAR(150) NOT NULL,
    data_nascimento  DATE,
    parentesco       VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_dependente_beneficiario
        FOREIGN KEY (beneficiario_id) REFERENCES beneficiario (id) ON DELETE CASCADE
);

CREATE INDEX idx_dependente_beneficiario ON dependente (beneficiario_id);
