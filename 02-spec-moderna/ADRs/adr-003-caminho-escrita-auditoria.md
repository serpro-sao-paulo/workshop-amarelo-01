<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# ADR-003: Caminho de Escrita da Trilha de `AUDITORIA`

![ESTÁGIO 02 Spec Moderna](https://img.shields.io/badge/ESTÁGIO-02%20Spec%20Moderna-00A4EF?style=for-the-badge) ![ADR MADR](https://img.shields.io/badge/ADR-MADR-1A1A1A?style=for-the-badge) ![STATUS Aceita](https://img.shields.io/badge/STATUS-Aceita-2E7D32?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../../README.md) → [Estágio 2](../README.md) → **ADRs** → **ADR-003**

> Gerado por `/generate-adr` em 2026-06-10. Decisão ratificada pela equipe.

## Status

Aceita

## Data

2026-06-10

## Contexto

A trilha `AUDITORIA` (log append-only com SEQ-AUDIT, USUARIO, ACAO, TABELA-REF, CHAVE-REF,
VLR-ANTERIOR/NOVO) é **dado próprio do contexto de Relatórios e Auditoria**
([`bounded-contexts.md`](../bounded-contexts.md)). Porém, no legado, quem **escreve** em `AUDITORIA` é
`BATCHCON` — programa do contexto de **Processamento de Pagamentos** — ao conciliar (`CO`) e ao
registrar divergências (`DV`) ([`business-rules-catalog.md`, BATCHCON #5](../../01-arqueologia/business-rules-catalog.md);
[`dependency-map.md`, BATCHCON→AUDITORIA](../../01-arqueologia/dependency-map.md)).

Ou seja: um contexto precisa registrar auditoria em dado pertencente a outro contexto. Precisamos
decidir o caminho de escrita antes de definir contratos, para não reintroduzir acoplamento por dado
compartilhado. Afeta diretamente REQ-028, REQ-029 e o domínio de ações REQ-032.

## Decisão

A equipe escolheu **expor uma interface `AuditLog.record(evento)` pelo contexto de Relatórios e
Auditoria**, consumida in-process por qualquer contexto que precise auditar (hoje, Pagamentos).

**Justificativa:** mantém `AUDITORIA` como dado privado de um único dono de escrita (Relatórios e
Auditoria), evitando escrita cruzada direta no DDM/tabela e preservando a fronteira de bounded context;
os demais contextos auditam por contrato explícito, não por acesso a dados alheios.

## Opções Consideradas

### Opção 1: Interface `AuditLog.record()` exposta por Relatórios e Auditoria

- **Descrição:** Relatórios e Auditoria publica uma porta (interface) `AuditLog.record(evento)`;
  Pagamentos chama essa porta in-process ao conciliar/divergir.
- **Prós:**
  - `AUDITORIA` permanece com dono de escrita único; fronteira de contexto intacta.
  - Regras de auditoria (formato, ações válidas REQ-032, futura exibição de `EX`) centralizadas em um
    só lugar.
  - Desacopla o produtor do evento do schema de armazenamento.
- **Contras:**
  - Exige definir e versionar o contrato da porta de auditoria.
- **Risco:** baixo.
- **Esforço:** same.

### Opção 2: Shared kernel de auditoria

- **Descrição:** um módulo de auditoria compartilhado, importado por todos os contextos.
- **Prós:**
  - Reuso direto do código de gravação.
- **Contras:**
  - Shared kernel acopla todos os contextos ao mesmo modelo; mudanças no kernel impactam todos.
- **Risco:** médio — erosão de fronteiras ao longo do tempo.
- **Esforço:** same.

### Opção 3: Escrita direta no DDM/tabela `AUDITORIA`

- **Descrição:** cada contexto escreve diretamente na tabela de auditoria (espelha o legado).
- **Prós:**
  - Menor distância do legado.
- **Contras:**
  - Dois ou mais donos de escrita do mesmo dado; regras de auditoria espalhadas; difícil garantir a
    correção de compliance (ex.: nunca ocultar `EX`).
- **Risco:** alto para compliance (relaciona-se a MYS-029).
- **Esforço:** lower.

## Consequências

### Positivas

- Um único ponto de gravação e de regras de auditoria — base sólida para corrigir o problema de
  compliance MYS-029 (exclusões `EX` ocultas) de forma central.
- Pagamentos audita conciliações/divergências (REQ-028/029) sem tocar no dado de outro contexto.
- Contrato explícito facilita testes e evolução (ex.: novos tipos de evento).

### Negativas

- Introduz uma dependência de Pagamentos → Relatórios e Auditoria (porta de escrita) a versionar.
- Necessário garantir transação/consistência entre a operação de pagamento e o registro de auditoria.

## Requisitos Relacionados

- **Produção de eventos:** REQ-028, REQ-029 (Pagamentos)
- **Domínio de ações / trilha:** REQ-032 (Relatórios e Auditoria)
- **Mistério relacionado:** MYS-029 (exclusões ocultas — a centralização habilita a correção; a
  decisão de exibir `EX` permanece em Open Questions OQ-009)

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="adr-002-mapeamento-adabas-mu-pe-jpa.md"><strong>ADR-002</strong></a><br/>
<sub>Adabas MU/PE → JPA.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="adr-004-comunicacao-inter-context.md"><strong>ADR-004</strong></a><br/>
<sub>Comunicação inter-context.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../../README.md">Voltar ao Kit PT-BR</a></sub>
