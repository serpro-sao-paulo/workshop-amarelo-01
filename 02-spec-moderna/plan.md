# Implementation Plan: Modernização SIFAP (Modular Monolith)

**Branch**: `developer` (feature dir pinada via `.specify/feature.json` → `02-spec-moderna/`) | **Date**: 2026-06-11 | **Spec**: [SPECIFICATION.md](SPECIFICATION.md)

**Input**: Especificação EARS em [`02-spec-moderna/SPECIFICATION.md`](SPECIFICATION.md) (33 requisitos), bounded contexts em [`bounded-contexts.md`](bounded-contexts.md), decisões em [`ADRs/`](ADRs/).

> **Nota:** o `setup-plan.sh` reporta `FEATURE_SPEC` como `spec.md` por convenção, mas a spec canônica deste workshop é `SPECIFICATION.md`. Este plano lê `SPECIFICATION.md`.

## Summary

Modernizar o SIFAP (Natural/Adabas, 29 anos) para um **Modular Monolith** Java 21 + Spring Boot 3.3 + PostgreSQL 16, com frontend Next.js 15. A aplicação expõe REST API `/api/v1` (REQ-033) sobre 5 bounded contexts in-process: Cadastro de Beneficiários, Programas Sociais e Elegibilidade, Cálculo de Benefícios (motor sem estado — ADR-001), Processamento de Pagamentos (dono de `PAGAMENTO`) e Relatórios e Auditoria (dono de `AUDITORIA`). Comunicação por **domain events** in-process (ADR-004), com queries síncronas via interface. Grupos periódicos Adabas (DEPENDENTES, DESCONTOS) mapeados como `@OneToMany` (ADR-002); auditoria gravada por porta `AuditLog.record()` (ADR-003); segurança OAuth2/JWT com exceções legadas sob role explícita e auditada (ADR-005).

**Escopo desta iteração:** estrutura de dados, contratos de API e regras **confirmadas** (REQ-001 a REQ-033). As **fórmulas financeiras nucleares** (benefício, 13º, fonte da verdade do desconto) permanecem `NEEDS CLARIFICATION` (OQ-002/003/004) e **não** são implementadas até validação de domínio.

## Technical Context

**Language/Version**: Java 21 (records, sealed interfaces, pattern matching, virtual threads); TypeScript 5 (strict) no frontend.

**Primary Dependencies**: Spring Boot 3.3 (Web, Data JPA, Security, Validation, Events), Hibernate, springdoc-openapi (Swagger), Flyway (migrations); Next.js 15 (App Router) + Tailwind + shadcn/ui.

**Storage**: PostgreSQL 16. Grupos periódicos Adabas → tabelas filhas `@OneToMany` (ADR-002). 4 agregados raiz: `BENEFICIARIO`, `PROGRAMA_SOCIAL`, `PAGAMENTO`, `AUDITORIA`.

**Testing**: JUnit 5 + Testcontainers (backend, incl. testes de equivalência contra o legado); Vitest + Testing Library (frontend). TDD enquanto implementa (regra rígida do repo).

**Target Platform**: Linux server (containers Docker + Docker Compose para paridade local); deploy Azure via Terraform (fora do escopo deste plano de aplicação).

**Project Type**: Web application (backend Java + frontend Next.js) — Modular Monolith, package-by-feature.

**Performance Goals**: NEEDS CLARIFICATION — sem metas formais no legado; o batch mensal processa beneficiários ordenados por CPF (REQ-025) e a ordenação deve ser preservada para sistemas a jusante.

**Constraints**:
- Comunicação inter-módulo **in-process** (sem HTTP entre contextos) — Modular Monolith.
- Valores monetários **truncados** a 2 casas (REQ-019) — divergência com arredondamento de relatório a resolver (MYS-026).
- Ordenação por CPF no ciclo mensal é contrato de integração (REQ-025).
- Mascaramento de CPF (LGPD) deve ser único e sem vazamento (substitui MYS-027).

**Scale/Scope**: 33 requisitos EARS em 5 bounded contexts + 1 greenfield. 15 programas legados de origem; 4 DDMs.

### Áreas NEEDS CLARIFICATION (bloqueiam implementação parcial — ver Open Questions na spec)

| Item | Open Question | Impacto no plano |
| --- | --- | --- |
| Fórmula nuclear do benefício (multiplicativa vs aditiva) | OQ-002 / MYS-010 | `CalculoBeneficio` fica como **interface/porta sem corpo de fórmula**; data-model e contratos prosseguem. |
| Fórmula do 13º | OQ-003 / MYS-012 | idem — campo `tipoPagamento=D` modelado, cálculo deferido. |
| Fonte da verdade do desconto (3% vs 3/5/7/9%) | OQ-004 / MYS-013 | teto de 30% e exceção judicial (REQ-021/022) são confirmados; o cálculo por faixa fica deferido. |
| Backdoors (região 99, CPF 000/prefixos) | OQ-006/007/008 | ADR-005 define direção (role explícita); especificação por exceção pendente. |
| Exclusões `EX` na trilha | OQ-009 / MYS-029 | auditoria modelada como append-only; política de exibição de `EX` pendente. |

## Constitution Check

*GATE: deve passar antes da Phase 0; re-checado após a Phase 1.*

> O arquivo `.specify/memory/constitution.md` está como template não preenchido. Os gates abaixo derivam das **regras de governança de fato** do repositório: [`.github/copilot-instructions.md`](../.github/copilot-instructions.md) ("Regras de Geração de Código", "Regras de Segurança", "Regras Rígidas") e o fluxo SDD do workshop.

| Gate | Regra | Status |
| --- | --- | --- |
| Rastreabilidade legado | Todo requisito tem `source_legacy:` (`.NSN`/`.ddm` ou `[GREENFIELD]+justificativa`); CI `legacy-traceability` | ✅ PASS — 33/33 REQ com `source_legacy` em SPECIFICATION.md |
| EARS + REQ-ID | Todo requisito em EARS com REQ-NNN único | ✅ PASS |
| Test-first | Testes escritos enquanto implementa; equivalência contra legado | ✅ PASS (plano) — Testcontainers + testes de equivalência previstos |
| Sem segredos hardcoded | Secrets só via Key Vault / config externa | ✅ PASS (plano) — OAuth2/JWT, sem credenciais no código (ADR-005) |
| SQL só via JPA/JPQL | Sem concatenação de string SQL | ✅ PASS (plano) — Spring Data JPA |
| Mascarar dados sensíveis | CPF/valores nunca em log; mascaramento único | ✅ PASS (plano) — substitui MYS-027 |
| ADR para dependências/decisões | Decisões com ≥2 opções viram ADR | ✅ PASS — ADR-001 a ADR-005 |
| Não gerar código nuclear sem regra confirmada | Fórmulas financeiras sem fonte confirmada não viram código | ✅ PASS — fórmulas marcadas NEEDS CLARIFICATION, não implementadas |

**Resultado:** PASS com áreas explicitamente deferidas (NEEDS CLARIFICATION). Nenhuma violação a justificar em Complexity Tracking.

## Project Structure

### Documentation (this feature)

```text
02-spec-moderna/
├── SPECIFICATION.md     # Spec EARS canônica (33 REQ)
├── bounded-contexts.md  # 5 contextos
├── ADRs/                # ADR-001..005
├── plan.md              # Este arquivo (/speckit.plan)
├── research.md          # Phase 0 (/speckit.plan)
├── data-model.md        # Phase 1 (/speckit.plan)
├── quickstart.md        # Phase 1 (/speckit.plan)
├── contracts/           # Phase 1 (/speckit.plan)
└── tasks.md             # Phase 2 (/speckit.tasks — NÃO criado aqui)
```

### Source Code (repository root)

```text
backend/
├── src/main/java/br/gov/sifap/
│   ├── beneficiario/        # Bounded context: Cadastro de Beneficiários
│   │   ├── domain/          #   Beneficiario, Dependente (entidades + regras)
│   │   ├── application/     #   serviços, portas (BeneficiarioQuery)
│   │   ├── infrastructure/  #   repositórios JPA, validação CPF mod-11
│   │   └── api/             #   REST controllers /api/v1/beneficiarios
│   ├── programa/            # Bounded context: Programas Sociais e Elegibilidade
│   │   ├── domain/          #   ProgramaSocial, regras de elegibilidade
│   │   ├── application/     #   ElegibilidadeService (porta)
│   │   ├── infrastructure/
│   │   └── api/             #   /api/v1/programas, /api/v1/elegibilidade
│   ├── calculo/             # Bounded context: Cálculo (motor SEM ESTADO — ADR-001)
│   │   ├── domain/          #   CalculoBeneficio (interface; fórmula DEFERIDA)
│   │   └── application/     #   descontos: teto 30% + exceção judicial (confirmado)
│   ├── pagamento/           # Bounded context: Processamento de Pagamentos (dono PAGAMENTO)
│   │   ├── domain/          #   Pagamento (ciclo G→P/D/E), eventos de domínio
│   │   ├── application/     #   FolhaService, ConciliacaoService (CNAB 240)
│   │   ├── infrastructure/  #   repositório, parser CNAB, outbox de eventos
│   │   └── api/             #   /api/v1/pagamentos
│   ├── auditoria/           # Bounded context: Relatórios e Auditoria (dono AUDITORIA)
│   │   ├── domain/          #   EventoAuditoria (append-only)
│   │   ├── application/     #   AuditLog (porta record()), relatórios/read models
│   │   ├── infrastructure/
│   │   └── api/             #   /api/v1/relatorios, /api/v1/auditoria, /api/v1/consultas
│   ├── shared/              # Shared kernel mínimo: Money (truncamento REQ-019), CPF VO
│   └── config/              # SecurityConfig (OAuth2/JWT — ADR-005), OpenAPI
├── src/main/resources/db/migration/   # Flyway (expand-contract)
└── src/test/java/...        # JUnit 5 + Testcontainers (incl. equivalência legado)

frontend/
├── src/app/                 # Next.js 15 App Router
│   ├── beneficiarios/
│   ├── programas/
│   ├── pagamentos/
│   └── relatorios/
├── src/components/          # shadcn/ui
└── src/services/            # clientes da API /api/v1
```

**Structure Decision**: Modular Monolith **package-by-feature** — um pacote Java por bounded context (`beneficiario`, `programa`, `calculo`, `pagamento`, `auditoria`), comunicação in-process via portas + domain events (ADR-004), `shared` mínimo para Value Objects transversais (Money com truncamento REQ-019, CPF). Frontend Next.js separado consumindo `/api/v1`.

## Complexity Tracking

> Sem violações constitucionais a justificar. O motor de Cálculo sem estado (ADR-001) e o `shared` mínimo são simplificações, não complexidade adicional. Domain events (ADR-004) introduzem consistência eventual — tratada com transactional outbox, registrada como consequência no ADR-004 (não é violação de gate).
