# Quickstart — Validação da Modernização SIFAP

> Guia de **validação executável** (não implementação). Prova, ponta a ponta, que as partes **confirmadas** do plano funcionam. Cenários que dependem de fórmulas deferidas (D-01..D-03) ficam marcados como ⏸️ **bloqueado** até resolução das Open Questions. Detalhes de modelo/contrato estão em [data-model.md](data-model.md) e [contracts/openapi.yaml](contracts/openapi.yaml).

## Pré-requisitos

- Java 21, Maven/Gradle, Node 20+, Docker + Docker Compose.
- PostgreSQL 16 via Docker Compose (paridade local).
- Backend Spring Boot 3.3 + frontend Next.js 15 (criados na implementação — Estágio 3).

## Setup

```bash
# 1. Subir banco + serviços de apoio
docker compose up -d postgres

# 2. Backend (porta 8080), aplica migrations Flyway (expand-contract)
cd backend && ./mvnw spring-boot:run

# 3. Frontend (porta 3000)
cd frontend && npm install && npm run dev

# 4. OpenAPI/Swagger UI
open http://localhost:8080/swagger-ui.html
```

## Cenários de validação

Cada cenário rastreia para requisitos EARS de [SPECIFICATION.md](SPECIFICATION.md). Comandos `curl` assumem um JWT válido em `$TOKEN` (ADR-005).

### ✅ C1 — Cadastrar beneficiário com CPF válido (REQ-001/002/003)

```bash
curl -X POST http://localhost:8080/api/v1/beneficiarios \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"cpf":"<CPF_VALIDO>","nome":"Maria Silva","dataNascimento":"1980-05-10","regiao":"01"}'
```

- **Esperado:** `201 Created`; corpo com `id`; CPF persistido após validação mod-11.

### ✅ C2 — Rejeitar CPF inválido / duplicado (REQ-001/002)

```bash
# CPF inválido → 400
curl -X POST .../beneficiarios -d '{"cpf":"11111111111", ...}'
# CPF já existente → 409
```

- **Esperado:** `400` para mod-11 inválido; `409 Conflict` para CPF duplicado.

### ✅ C3 — CPF mascarado em listagem (LGPD, substitui MYS-027)

```bash
curl http://localhost:8080/api/v1/beneficiarios -H "Authorization: Bearer $TOKEN"
```

- **Esperado:** `200`; cada `cpf` aparece **mascarado** (ex.: `***.***.789-**`); nenhum CPF em texto claro em logs.

### ✅ C4 — Gerenciar dependentes (REQ-009/010/011)

```bash
curl http://localhost:8080/api/v1/beneficiarios/{id}/dependentes -H "Authorization: Bearer $TOKEN"
```

- **Esperado:** `200`; dependentes vêm de tabela filha `@OneToMany` (ADR-002), não de colunas repetidas.

### ✅ C5 — Avaliar elegibilidade (REQ-012..016)

```bash
curl -X POST http://localhost:8080/api/v1/elegibilidade \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"beneficiarioId":"<uuid>","programaId":"<uuid>"}'
```

- **Esperado:** `200`; `{ "elegivel": true|false, "motivos": [...] }` segundo critérios confirmados do programa.

### ✅ C6 — Ordenação por CPF na geração de folha (REQ-025)

```bash
curl -X POST http://localhost:8080/api/v1/pagamentos/folha \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"competencia":"2026-06"}'
```

- **Esperado:** `202 Accepted`; a folha gerada lista pagamentos **ordenados por CPF** (contrato de integração). *Validar a ordenação, não os valores.*

### ✅ C7 — Teto de desconto 30% + exceção judicial (REQ-021/022)

- **Esperado:** soma de descontos ordinários ≤ 30% da renda; descontos do tipo `judicial` podem exceder o teto. *Estrutura confirmada; faixa percentual deferida (D-03).*

### ✅ C8 — Conciliação CNAB atualiza status (REQ-027/028)

```bash
curl -X POST http://localhost:8080/api/v1/pagamentos/conciliacao \
  -H "Authorization: Bearer $TOKEN" --data-binary @retorno.cnab
```

- **Esperado:** `202`; `status` dos pagamentos correspondentes muda `G`→`P`/`D`/`E`; `dataConciliacao` preenchida.

### ✅ C9 — Trilha de auditoria append-only (REQ-028/029/032, ADR-003)

```bash
curl "http://localhost:8080/api/v1/auditoria?entidade=PAGAMENTO&entidadeId=<uuid>" \
  -H "Authorization: Bearer $TOKEN"
```

- **Esperado:** `200`; evento gerado pela conciliação aparece; eventos são imutáveis (sem update/delete); gravados via porta `AuditLog.record()`.

### ✅ C10 — Acesso negado por padrão (deny-by-default, ADR-005)

- **Esperado:** chamada sem JWT → `401`; JWT sem role adequada → `403`. Exceções legadas (região 99 etc.) exigem role explícita e geram evento de auditoria.

### ⏸️ C11 — Valor do benefício (BLOQUEADO — OQ-002/003/004)

- **Bloqueado:** a fórmula nuclear do benefício (D-01), o 13º (D-02) e a fonte da verdade das faixas de desconto (D-03) **não estão confirmados**. Não validar valores monetários calculados até resolução das Open Questions. Quando resolvido, adicionar testes de **equivalência** comparando saída Java vs legado Natural.

## Testes automatizados de referência

| Camada | Ferramenta | Foco |
| --- | --- | --- |
| Unit | JUnit 5 | regras confirmadas (CPF mod-11, teto 30%, ordenação) |
| Integração | Testcontainers (PostgreSQL 16) | repositórios JPA, migrations Flyway, mapeamento `@OneToMany` |
| Equivalência | JUnit 5 + dataset legado | ⏸️ pós-resolução de D-01..D-03 |
| Frontend | Vitest + Testing Library | telas de cadastro/consulta |

## Critério de pronto (desta validação)

- [ ] C1..C10 passam.
- [ ] CPF nunca aparece em claro em logs.
- [ ] Folha respeita ordenação por CPF.
- [ ] Auditoria é append-only e só gravada via porta.
- [ ] C11 permanece bloqueado e documentado até OQ-002/003/004 serem respondidas pela equipe.
