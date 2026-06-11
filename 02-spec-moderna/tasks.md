---
description: "Task list — Modernização SIFAP (Modular Monolith)"
---

# Tasks: Modernização SIFAP (Modular Monolith)

**Input**: Design documents em [`02-spec-moderna/`](.) — [plan.md](plan.md), [SPECIFICATION.md](SPECIFICATION.md), [research.md](research.md), [data-model.md](data-model.md), [contracts/openapi.yaml](contracts/openapi.yaml)

**Tests**: INCLUÍDOS — o repositório exige test-first ("escreva-os enquanto implementa") e testes unitários obrigatórios para lógica de negócio ([copilot-instructions.md](../.github/copilot-instructions.md)).

**Organização**: tarefas agrupadas por user story (= bounded context) para implementação e teste independentes. As user stories foram priorizadas por ordem de dependência de domínio.

## Mapa user story → bounded context → requisitos

| Story | Bounded context | Requisitos | Prioridade |
| --- | --- | --- | --- |
| US1 | Cadastro de Beneficiários | REQ-001..011 | P1 (MVP) |
| US2 | Programas Sociais e Elegibilidade | REQ-012..017 | P2 |
| US3 | Cálculo + Processamento de Pagamentos | REQ-018..029 | P3 |
| US4 | Relatórios e Auditoria | REQ-030..032 | P4 |

> REQ-033 (REST API `/api/v1`, greenfield) é transversal: o esqueleto é montado no Setup/Foundational e cada story adiciona seus controllers.

> ⏸️ **DEFERIDO** = depende de Open Question não resolvida (OQ-002/003/004 — fórmulas financeiras; OQ-006/007/008 — backdoors). Ver [research.md](research.md) D-01..D-06. Não implementar valor/lógica até ratificação da equipe.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: pode rodar em paralelo (arquivos diferentes, sem dependência pendente)
- **[Story]**: a qual user story a tarefa pertence (US1..US4)
- Caminhos de arquivo são exatos

**Path conventions** (Web app, ver plan.md): backend `backend/src/main/java/br/gov/sifap/<contexto>/`, testes `backend/src/test/java/br/gov/sifap/<contexto>/`, frontend `frontend/src/`.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: inicialização do projeto e estrutura básica.

- [X] T001 Criar estrutura do monorepo (`backend/` + `frontend/`) conforme plan.md
- [X] T002 Inicializar backend Spring Boot 3.3 (Java 21) com dependências Web, Data JPA, Security, Validation, springdoc-openapi, Flyway em `backend/pom.xml`
- [X] T003 [P] Inicializar frontend Next.js 15 (TypeScript strict, Tailwind, shadcn/ui) em `frontend/`
- [X] T004 [P] Configurar Docker Compose com PostgreSQL 16 em `docker-compose.yml`
- [ ] T005 [P] Configurar lint/format do backend (Spotless + Checkstyle) em `backend/pom.xml`
- [X] T006 [P] Configurar lint/format do frontend (ESLint + Prettier) e `tsconfig.json` strict em `frontend/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: infraestrutura central que DEVE existir antes de qualquer user story.

**⚠️ CRÍTICO**: nenhuma user story começa antes desta fase concluir.

- [X] T007 Configurar framework de migrations Flyway (expand-contract) e baseline em `backend/src/main/resources/db/migration/`
- [X] T008 [P] Implementar Value Object `Money` (BigDecimal escala 2, `RoundingMode.DOWN` — REQ-019) em `backend/src/main/java/br/gov/sifap/shared/Money.java`
- [X] T009 [P] Implementar Value Object `CPF` (validação módulo 11 + mascaramento — REQ-001/002) em `backend/src/main/java/br/gov/sifap/shared/CPF.java`
- [X] T010 [P] Configurar `SecurityConfig` OAuth2/JWT deny-by-default (ADR-005, REQ-033) em `backend/src/main/java/br/gov/sifap/config/SecurityConfig.java`
- [X] T011 [P] Configurar OpenAPI/springdoc e base `/api/v1` (REQ-033) em `backend/src/main/java/br/gov/sifap/config/OpenApiConfig.java`
- [X] T012 Configurar tratamento global de erros (`@RestControllerAdvice`) + respostas de validação em `backend/src/main/java/br/gov/sifap/config/GlobalExceptionHandler.java`
- [ ] T013 [P] Configurar logging estruturado com mascaramento de CPF/valores em `backend/src/main/java/br/gov/sifap/config/LoggingConfig.java`
- [X] T014 Configurar infraestrutura de domain events (`ApplicationEventPublisher`) + esqueleto de transactional outbox (ADR-004) em `backend/src/main/java/br/gov/sifap/shared/events/`
- [X] T015 [P] Configurar base de testes de integração com Testcontainers (PostgreSQL 16) em `backend/src/test/java/br/gov/sifap/support/`

**Checkpoint**: fundação pronta — user stories podem iniciar (em paralelo, se houver capacidade).

---

## Phase 3: User Story 1 — Cadastro de Beneficiários (Priority: P1) 🎯 MVP

**Goal**: cadastrar, validar e consultar beneficiários e seus dependentes, com CPF validado e único.

**Independent Test**: criar beneficiário com CPF válido (`201`), rejeitar CPF inválido (`400`) e duplicado (`409`), listar com CPF mascarado, gerenciar dependentes via tabela filha.

### Tests for User Story 1 ⚠️ (escrever e ver FALHAR antes de implementar)

- [X] T016 [P] [US1] Teste de contrato POST/GET `/api/v1/beneficiarios` contra `contracts/openapi.yaml` em `backend/src/test/java/br/gov/sifap/beneficiario/api/BeneficiarioContractTest.java`
- [X] T017 [P] [US1] Testes unitários de validação CPF módulo 11 (REQ-002/003) em `backend/src/test/java/br/gov/sifap/shared/CPFTest.java`
- [X] T018 [P] [US1] Teste de integração cadastro + dependentes `@OneToMany` (REQ-007/009/010) em `backend/src/test/java/br/gov/sifap/beneficiario/BeneficiarioIntegrationTest.java`

### Implementation for User Story 1

- [X] T019 [P] [US1] Migration Flyway tabelas `beneficiario` e `dependente` (ADR-002) em `backend/src/main/resources/db/migration/V2__beneficiario.sql`
- [X] T020 [P] [US1] Entidade `Beneficiario` + enum `SituacaoBeneficiario` (REQ-006/008) em `backend/src/main/java/br/gov/sifap/beneficiario/domain/`
- [X] T021 [P] [US1] Entidade `Dependente` + enum `Parentesco` (REQ-009/011) em `backend/src/main/java/br/gov/sifap/beneficiario/domain/`
- [X] T022 [US1] `BeneficiarioRepository` (Spring Data JPA) em `backend/src/main/java/br/gov/sifap/beneficiario/infrastructure/`
- [X] T023 [US1] `BeneficiarioService`: validação obrigatórios (REQ-001/004/005), CPF inválido (REQ-003), CPF duplicado (REQ-007), status inicial ativo (REQ-006), limite de dependentes (REQ-010) em `backend/src/main/java/br/gov/sifap/beneficiario/application/BeneficiarioService.java`
- [X] T024 [US1] Porta `BeneficiarioQuery` (consulta síncrona para outros contextos) em `backend/src/main/java/br/gov/sifap/beneficiario/application/BeneficiarioQuery.java`
- [X] T025 [US1] Controller REST `/api/v1/beneficiarios` e `/{id}/dependentes` com `@Valid` (REQ-033) em `backend/src/main/java/br/gov/sifap/beneficiario/api/BeneficiarioController.java`
- [ ] T026 [US1] Publicar domain event + chamar `AuditLog.record()` em criação/alteração em `backend/src/main/java/br/gov/sifap/beneficiario/application/BeneficiarioService.java`
- [ ] T027 [P] [US1] Telas de cadastro/consulta de beneficiários em `frontend/src/app/beneficiarios/`

**Checkpoint**: US1 funcional e testável de forma independente (MVP).

---

## Phase 4: User Story 2 — Programas Sociais e Elegibilidade (Priority: P2)

**Goal**: cadastrar programas e avaliar elegibilidade de um beneficiário segundo os critérios do programa.

**Independent Test**: cadastrar programa ativo, avaliar elegibilidade retornando `elegivel` + `motivos` segundo status, faixa etária, teto de renda e tipo de programa.

### Tests for User Story 2 ⚠️

- [ ] T028 [P] [US2] Teste de contrato `/api/v1/programas` e `/api/v1/elegibilidade` em `backend/src/test/java/br/gov/sifap/programa/api/ProgramaContractTest.java`
- [ ] T029 [P] [US2] Testes unitários das regras de elegibilidade (REQ-013/014/015/016/017) em `backend/src/test/java/br/gov/sifap/programa/ElegibilidadeServiceTest.java`

### Implementation for User Story 2

- [ ] T030 [P] [US2] Migration Flyway tabela `programa_social` em `backend/src/main/resources/db/migration/V3__programa_social.sql`
- [ ] T031 [P] [US2] Entidade `ProgramaSocial` + embeddable de critérios (faixa etária REQ-015, teto renda REQ-016, tipo REQ-017) em `backend/src/main/java/br/gov/sifap/programa/domain/`
- [ ] T032 [US2] `ProgramaSocialRepository` em `backend/src/main/java/br/gov/sifap/programa/infrastructure/`
- [ ] T033 [US2] `ProgramaSocialService`: status ativo ao incluir (REQ-012) em `backend/src/main/java/br/gov/sifap/programa/application/ProgramaSocialService.java`
- [ ] T034 [US2] Porta `ElegibilidadeService.avaliar()`: programa ativo (REQ-013), status do beneficiário via `BeneficiarioQuery` (REQ-014), faixa etária (REQ-015), teto de renda (REQ-016), regras por tipo (REQ-017) em `backend/src/main/java/br/gov/sifap/programa/application/ElegibilidadeService.java`
- [ ] T035 [US2] Controllers REST `/api/v1/programas` e `/api/v1/elegibilidade` (REQ-033) em `backend/src/main/java/br/gov/sifap/programa/api/`
- [ ] T036 [P] [US2] Telas de programas e simulação de elegibilidade em `frontend/src/app/programas/`

**Checkpoint**: US1 e US2 funcionam de forma independente.

---

## Phase 5: User Story 3 — Cálculo + Processamento de Pagamentos (Priority: P3)

**Goal**: gerar folha de pagamento ordenada por CPF, aplicar descontos com teto, e conciliar retorno bancário. Cálculo é motor **sem estado** (ADR-001); Pagamentos é o único escritor de `PAGAMENTO`.

**Independent Test**: gerar folha ordenada por CPF apenas para beneficiários ativos; respeitar teto de 30% (exceto judicial); conciliar CNAB atualizando status e registrando auditoria.

> ⏸️ As **fórmulas financeiras** (valor nuclear, 13º, faixas de desconto, IPCA) ficam DEFERIDAS (OQ-002/003/004, MYS-014). Modelar estrutura e portas; **não** ligar o cálculo de valor até ratificação.

### Tests for User Story 3 ⚠️

- [ ] T037 [P] [US3] Teste de contrato `/api/v1/pagamentos` (folha, conciliação, consulta) em `backend/src/test/java/br/gov/sifap/pagamento/api/PagamentoContractTest.java`
- [ ] T038 [P] [US3] Testes unitários de desconto: teto 30% (REQ-021/023) e judicial sem teto (REQ-022) em `backend/src/test/java/br/gov/sifap/pagamento/DescontoServiceTest.java`
- [ ] T039 [P] [US3] Teste de integração da folha ordenada por CPF e apenas ativos (REQ-018/025/026) em `backend/src/test/java/br/gov/sifap/pagamento/FolhaIntegrationTest.java`
- [ ] T040 [P] [US3] Teste de integração da conciliação CNAB: status por código de retorno e divergência (REQ-027/028) em `backend/src/test/java/br/gov/sifap/pagamento/ConciliacaoIntegrationTest.java`

### Implementation for User Story 3

- [ ] T041 [P] [US3] Migration Flyway tabelas `pagamento` e `desconto` (ADR-002) em `backend/src/main/resources/db/migration/V4__pagamento.sql`
- [ ] T042 [P] [US3] Entidade `Pagamento` + enums `StatusPagamento`/`TipoPagamento` (domínio REQ-030) em `backend/src/main/java/br/gov/sifap/pagamento/domain/`
- [ ] T043 [P] [US3] Entidade `Desconto` + enum `TipoDesconto` (ordinário/judicial) em `backend/src/main/java/br/gov/sifap/pagamento/domain/`
- [ ] T044 [US3] Interface stateless `CalculoBeneficio` (ADR-001) — ⏸️ **corpo da fórmula DEFERIDO** (D-01/OQ-002) em `backend/src/main/java/br/gov/sifap/calculo/domain/CalculoBeneficio.java`
- [ ] T045 [US3] `DescontoService`: teto de 30% do bruto (REQ-021/023), judicial sem teto (REQ-022) em `backend/src/main/java/br/gov/sifap/calculo/application/DescontoService.java`
- [ ] T046 [US3] ⏸️ **DEFERIDO** — correção monetária IPCA (REQ-020/MYS-014) e abono natalino/13º (REQ-024/D-02): criar stubs com `NEEDS CLARIFICATION` em `backend/src/main/java/br/gov/sifap/calculo/application/`
- [ ] T047 [US3] `PagamentoRepository` em `backend/src/main/java/br/gov/sifap/pagamento/infrastructure/`
- [ ] T048 [US3] `FolhaService`: ordenação por CPF (REQ-025), apenas beneficiário ativo (REQ-018/026), orquestra Cálculo stateless e é o único escritor de `PAGAMENTO` (ADR-001) em `backend/src/main/java/br/gov/sifap/pagamento/application/FolhaService.java`
- [ ] T049 [US3] `ConciliacaoService`: parser CNAB, atualização de status por código de retorno (REQ-027), detecção de divergência (REQ-028), auditoria na conciliação (REQ-029) em `backend/src/main/java/br/gov/sifap/pagamento/application/ConciliacaoService.java`
- [ ] T050 [US3] Controller REST `/api/v1/pagamentos` (`/folha`, `/conciliacao`, consulta) (REQ-033) em `backend/src/main/java/br/gov/sifap/pagamento/api/PagamentoController.java`
- [ ] T051 [US3] Publicar domain events de pagamento/conciliação → `AuditLog` com idempotência/outbox (ADR-003/004) em `backend/src/main/java/br/gov/sifap/pagamento/application/`
- [ ] T052 [P] [US3] Telas de consulta de pagamentos e geração de folha em `frontend/src/app/pagamentos/`

**Checkpoint**: US1, US2 e US3 funcionam de forma independente (cálculo de valor permanece deferido).

---

## Phase 6: User Story 4 — Relatórios e Auditoria (Priority: P4)

**Goal**: expor trilha de auditoria append-only e relatórios com rótulos de status corretos.

**Independent Test**: gravar evento somente via porta `AuditLog.record()`; consultar trilha imutável; relatório de pagamentos e consulta de beneficiários com rótulos corretos.

### Tests for User Story 4 ⚠️

- [ ] T053 [P] [US4] Teste de contrato `/api/v1/relatorios` e `/api/v1/auditoria` em `backend/src/test/java/br/gov/sifap/auditoria/api/AuditoriaContractTest.java`
- [ ] T054 [P] [US4] Teste de integração auditoria append-only via porta `AuditLog` (REQ-032, ADR-003) em `backend/src/test/java/br/gov/sifap/auditoria/AuditLogIntegrationTest.java`

### Implementation for User Story 4

- [ ] T055 [P] [US4] Migration Flyway tabela `evento_auditoria` (append-only) em `backend/src/main/resources/db/migration/V5__auditoria.sql`
- [ ] T056 [P] [US4] Entidade `EventoAuditoria` + enum `AcaoAuditoria` (REQ-032) em `backend/src/main/java/br/gov/sifap/auditoria/domain/`
- [ ] T057 [US4] Implementar porta `AuditLog.record()` (único escritor de `AUDITORIA`, ADR-003) em `backend/src/main/java/br/gov/sifap/auditoria/application/AuditLog.java`
- [ ] T058 [US4] Read models / serviços de relatório: rótulos de status de pagamento (REQ-030) e de beneficiário (REQ-031) em `backend/src/main/java/br/gov/sifap/auditoria/application/RelatorioService.java`
- [ ] T059 [US4] Controllers REST `/api/v1/relatorios`, `/api/v1/auditoria`, `/api/v1/consultas` (REQ-033) em `backend/src/main/java/br/gov/sifap/auditoria/api/`
- [ ] T060 [P] [US4] Telas de relatórios e trilha de auditoria em `frontend/src/app/relatorios/`

**Checkpoint**: todas as user stories funcionam de forma independente.

---

## Phase 7: Polish & Cross-Cutting Concerns

- [ ] T061 [P] Finalizar documentação OpenAPI/Swagger de todos os endpoints
- [ ] T062 [P] Auditar mascaramento de CPF/valores em logs e respostas (LGPD, substitui MYS-027)
- [ ] T063 ⏸️ **DEFERIDO** — harness de testes de equivalência Java vs legado Natural (bloqueado em D-01..D-03) em `backend/src/test/java/br/gov/sifap/equivalencia/`
- [ ] T064 [P] Hardening de segurança: CORS explícito; ⏸️ testes de autorização das exceções legadas (ADR-005, OQ-006/007/008 — spec por exceção deferida)
- [ ] T065 Rodar validação do `quickstart.md` (cenários C1..C10)
- [ ] T066 [P] Testes unitários de frontend (Vitest + Testing Library) em `frontend/src/`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: sem dependências — pode iniciar imediatamente.
- **Foundational (Phase 2)**: depende do Setup — **BLOQUEIA** todas as user stories.
- **User Stories (Phase 3+)**: dependem da Foundational. US1 não depende das demais. US2 usa `BeneficiarioQuery` (US1) para status. US3 usa `BeneficiarioQuery` (US1) e pode usar elegibilidade (US2). US4 recebe eventos de todas via `AuditLog`.
- **Polish (Phase 7)**: depende das user stories desejadas concluídas.

### User Story Dependencies

- **US1 (P1)**: após Foundational — sem dependências. MVP isolado.
- **US2 (P2)**: após Foundational — consulta status do beneficiário (porta de US1); testável de forma independente com stub da porta.
- **US3 (P3)**: após Foundational — consulta beneficiário (US1); cálculo de valor deferido.
- **US4 (P4)**: após Foundational — porta `AuditLog` é consumida por US1/US2/US3, mas a gravação/consulta é testável isoladamente.

### Within Each User Story

- Testes escritos e FALHANDO antes da implementação (TDD).
- Migrations/entidades → repositórios → serviços/portas → controllers → eventos/auditoria → frontend.

### Parallel Opportunities

- Todas as tarefas `[P]` do Setup podem rodar em paralelo.
- Todas as tarefas `[P]` da Foundational podem rodar em paralelo.
- Concluída a Foundational, as user stories podem ser tocadas em paralelo por pessoas diferentes (respeitando as portas entre contextos via stubs).
- Dentro de uma story, entidades e testes marcados `[P]` rodam em paralelo.

---

## Parallel Example: User Story 1

```text
# Após a Foundational, em paralelo (arquivos distintos):
T016 [P] [US1] Teste de contrato beneficiários
T017 [P] [US1] Testes unitários CPF mod-11
T018 [P] [US1] Teste de integração cadastro+dependentes
T019 [P] [US1] Migration beneficiario/dependente
T020 [P] [US1] Entidade Beneficiario + enum Situacao
T021 [P] [US1] Entidade Dependente + enum Parentesco
T027 [P] [US1] Telas de beneficiários (frontend)

# Sequencial (mesma área/dependência):
T022 → T023 → T024 → T025 → T026
```

---

## Implementation Strategy

- **MVP (entrega 1):** Phase 1 + Phase 2 + **US1** (Cadastro de Beneficiários). Entrega cadastro funcional com CPF validado, dependentes e API `/api/v1/beneficiarios`.
- **Incremento 2:** US2 (Programas e Elegibilidade).
- **Incremento 3:** US3 (Pagamentos) — **estrutura e conciliação** primeiro; **fórmulas de valor permanecem bloqueadas** até a equipe ratificar OQ-002/003/004.
- **Incremento 4:** US4 (Relatórios e Auditoria).
- **Polish:** documentação, mascaramento, hardening, equivalência (após resolução das Open Questions).

> **Gate de domínio:** não iniciar T044/T046/T063 (cálculo nuclear e equivalência) até as Open Questions de cálculo serem respondidas — regra rígida do repositório (não gerar código nuclear sem regra confirmada).
