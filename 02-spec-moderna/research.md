# Phase 0 — Research: Modernização SIFAP

> Consolida as decisões técnicas que resolvem (ou deferem explicitamente) os `NEEDS CLARIFICATION` do [plan.md](plan.md). Cada decisão referencia o ADR e/ou requisito de origem. Itens deferidos apontam para a Open Question correspondente em [SPECIFICATION.md](SPECIFICATION.md).

## R-01 — Estilo arquitetural: Modular Monolith vs microsserviços

- **Decision:** Modular Monolith com package-by-feature; um pacote por bounded context, comunicação **in-process**.
- **Rationale:** O mapa de dependências legado mostra **zero arestas programa→programa** (sem CALLNAT/INCLUDE); o acoplamento é só por dados compartilhados (`BENEFICIARIO` lido por 9 programas). Isso favorece um monólito modular com fronteiras lógicas claras, evitando o overhead operacional e a consistência distribuída de microsserviços num time único. Alinha-se à stack-alvo do repositório.
- **Alternatives considered:** Microsserviços (rejeitado — overhead de rede/observabilidade/transações distribuídas sem ganho de autonomia de time); pacote único sem fronteiras (rejeitado — perde os bounded contexts e a evolução independente).

## R-02 — Propriedade de escrita de PAGAMENTO

- **Decision:** Cálculo é **motor sem estado**; Processamento de Pagamentos é o **único escritor** de `PAGAMENTO`. (ADR-001)
- **Rationale:** No legado, 5 programas escrevem `PAGAMENTO` (CALCBENF/CALCCORR/CALCDSCT/BATCHPGT/BATCHCON). Tornar Cálculo sem estado elimina a escrita concorrente e resolve MYS-023 (ordem de aplicação de correção/desconto). Pagamentos orquestra: chama Cálculo (puro) → persiste.
- **Alternatives considered:** Cálculo dono de PAGAMENTO; co-propriedade. Ver ADR-001.

## R-03 — Mapeamento de grupos periódicos (PE) e múltiplos valores (MU) Adabas → JPA

- **Decision:** Grupos periódicos `DEPENDENTES` e `DESCONTOS` → `@OneToMany` em tabela filha própria. (ADR-002)
- **Rationale:** PE têm cardinalidade variável e atributos próprios; tabela filha normaliza, permite índice e consulta. MU simples (sem atributos) podem usar `@ElementCollection`/JSONB, mas os PE do SIFAP carregam dados estruturados (valor, tipo, origem do desconto).
- **Alternatives considered:** `@ElementCollection` (perde consulta rica), colunas repetidas C-001..C-N (anti-normalização), JSONB puro (perde integridade referencial). Ver ADR-002.

## R-04 — Caminho de escrita da auditoria

- **Decision:** Relatórios e Auditoria expõe a porta `AuditLog.record(evento)`; demais contextos chamam essa interface in-process. (ADR-003)
- **Rationale:** No legado, BATCHCON grava `AUDITORIA`. Centralizar a escrita atrás de uma porta dá ownership único do agregado `AUDITORIA` e desacopla os produtores. Combinada com R-05, a chamada pode ser síncrona (porta) e/ou via evento.
- **Alternatives considered:** Cada contexto escreve direto em AUDITORIA (rejeitado — múltiplos donos); tabela compartilhada (rejeitado — acoplamento de schema). Ver ADR-003.

## R-05 — Comunicação inter-context

- **Decision:** **Domain events** in-process (`ApplicationEventPublisher`), assíncronos para fatos de negócio; queries permanecem síncronas via porta. (ADR-004)
- **Rationale:** Eventos desacoplam produtor/consumidor dentro do mesmo processo, preservando a opção de extrair um módulo futuramente (Strangler Fig). Consultas (ex.: validar elegibilidade) precisam de resposta imediata → chamada síncrona à porta.
- **Consequences / mitigação:** Consistência eventual nos consumidores de evento → exige **transactional outbox** + idempotência para não perder/duplicar eventos (ex.: registro de auditoria de pagamento). Ver ADR-004.
- **Alternatives considered:** Chamadas síncronas diretas entre serviços (acoplamento temporal); message broker externo (overhead, contradiz monólito in-process).

## R-06 — Autenticação/autorização e backdoors legados

- **Decision:** OAuth2/JWT via Spring Security; os backdoors legados (região 99 / MYS-017, CPF iniciando 000 / MYS-019, prefixos especiais / MYS-021) são replicados **apenas** como exceções explícitas, sob role dedicada, deny-by-default e **auditadas**. (ADR-005)
- **Rationale:** Preservar comportamento legado observável sem perpetuar bypass implícito de segurança. Cada exceção vira regra de negócio nomeada, testada por testes de autorização.
- **Status:** Direção decidida; a **especificação detalhada de cada exceção** depende de validação de domínio (OQ-006/007/008) → **NEEDS CLARIFICATION**.
- **Alternatives considered:** Remover backdoors (risco de divergência de comportamento), manter implícitos (risco de segurança). Ver ADR-005.

## R-07 — Aritmética monetária

- **Decision:** `Money` como Value Object com `BigDecimal`, escala 2, **truncamento** (não arredondamento) conforme REQ-019.
- **Rationale:** REQ-019 confirma truncamento a 2 casas no legado; usar `BigDecimal` evita erros de ponto flutuante. `RoundingMode.DOWN` reproduz o truncamento.
- **Open issue:** MYS-026 — relatório aparenta **arredondar**, divergindo do cálculo que trunca. Resolver antes de finalizar relatórios financeiros → registrado como risco, não bloqueia estrutura.

## R-08 — Validação de CPF

- **Decision:** Value Object `CPF` com validação mod-11 na fronteira (entrada da API e ingestão batch), conforme REQ-001/002.
- **Rationale:** Centraliza a regra confirmada de validação; impede CPF inválido de entrar no domínio. Mascaramento de exibição/log centralizado no mesmo VO (substitui MYS-027, evitando vazamento).
- **Alternatives considered:** Validação espalhada por controller (duplicação, risco de divergência).

## R-09 — Migrações de banco

- **Decision:** Flyway com estratégia **expand-contract** (compatível com a `safe-migration` skill do repo).
- **Rationale:** Permite evoluir schema sem downtime e com rollback seguro; alinhado às `database.instructions.md`.
- **Alternatives considered:** `hibernate.ddl-auto=update` (rejeitado — não auditável/não versionado em produção).

## R-10 — Conciliação bancária (CNAB)

- **Decision:** Modelar `Pagamento` com ciclo de estados `G` (gerado) → `P` (pago) / `D` (devolvido/13º?) / `E` (erro/excluído), atualizado por arquivo de retorno bancário (REQ-026/027/028).
- **Rationale:** BATCHCON concilia retorno e atualiza status; o conjunto de códigos de status é confirmado parcialmente.
- **Open issue:** O significado exato de cada código de status e a separação 13º (`D`) vs devolução é ambíguo → ligado a OQ-003 (13º) e à semântica de status; tratar status como enum aberto até confirmação.

## Deferidos (NEEDS CLARIFICATION — não resolvidos nesta fase)

| ID | Tema | Open Question / Mistério | Por que não pode ser decidido agora |
| --- | --- | --- | --- |
| D-01 | Fórmula nuclear do benefício | OQ-002 / MYS-010 | Legado tem expressão ambígua (multiplicativa vs aditiva); sem fonte confirmada não se gera código financeiro. |
| D-02 | Cálculo do 13º | OQ-003 / MYS-012 | Gatilho e base de cálculo do 13º não confirmados. |
| D-03 | Faixas de desconto | OQ-004 / MYS-013 | Conflito entre 3% fixo e faixas 3/5/7/9%; teto 30% e exceção judicial **são** confirmados (REQ-021/022). |
| D-04 | Especificação dos backdoors | OQ-006/007/008 | Direção em ADR-005; regras por exceção exigem validação de domínio. |
| D-05 | Política de exclusões `EX` na trilha | OQ-009 / MYS-029 | Comportamento de exibição/retenção indefinido; auditoria modelada append-only por ora. |
| D-06 | Divergência truncar vs arredondar | MYS-026 | Precisa decidir a fonte da verdade entre cálculo e relatório. |

**Saída desta fase:** todos os `NEEDS CLARIFICATION` ou foram resolvidos (R-01..R-10) ou estão **explicitamente deferidos** com rastreabilidade a uma Open Question. As partes deferidas são todas **fórmulas/políticas**, não estrutura — portanto Phase 1 (data-model, contratos) pode prosseguir.
