<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# ADR-001: Ownership de Escrita de `PAGAMENTO` — Cálculo como Motor Sem Estado

![ESTÁGIO 02 Spec Moderna](https://img.shields.io/badge/ESTÁGIO-02%20Spec%20Moderna-00A4EF?style=for-the-badge) ![ADR MADR](https://img.shields.io/badge/ADR-MADR-1A1A1A?style=for-the-badge) ![STATUS Aceita](https://img.shields.io/badge/STATUS-Aceita-2E7D32?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../../README.md) → [Estágio 2](../README.md) → **ADRs** → **ADR-001**

> Gerado por `/generate-adr` em 2026-06-10. Decisão ratificada pela equipe.

## Status

Aceita

## Data

2026-06-10

## Contexto

No legado SIFAP, o arquivo `PAGAMENTO` é escrito por **cinco programas pertencentes a dois grupos
de responsabilidade distintos**, conforme [`dependency-map.md`](../../01-arqueologia/dependency-map.md):

- **Grupo Cálculo:** `CALCBENF` (`STORE`), `CALCCORR` (`UPDATE`), `CALCDSCT` (`UPDATE`)
- **Grupo Pagamentos:** `BATCHPGT` (`STORE`), `BATCHCON` (`UPDATE`)

O recorte de bounded contexts ([`bounded-contexts.md`](../bounded-contexts.md)) identificou essa
dupla escrita como **a decisão de fronteira central**: tanto o contexto de Cálculo de Benefícios (H3)
quanto o de Processamento de Pagamentos (H4) gravavam `PAGAMENTO`. Além disso, o mistério **MYS-023**
([`mysteries-found.md`](../../01-arqueologia/mysteries-found.md)) documenta que `BATCHPGT`
**reimplementa inline** toda a fórmula de `CALCBENF` (sem `CALLNAT`, pois há zero chamadas entre
programas no legado), criando uma **dupla fonte da verdade** para o cálculo do benefício — risco de
divergência sempre que um lado é alterado e o outro não.

Precisamos decidir **agora**, antes de escrever o data-model e os contratos, quem é o dono da escrita
de `PAGAMENTO` no Modular Monolith, porque essa fronteira define se o cálculo será uma dependência
persistente ou um serviço puro. A decisão restringe diretamente os requisitos de cálculo
(REQ-018 a REQ-024) e de pagamento (REQ-025 a REQ-029) em [`SPECIFICATION.md`](../SPECIFICATION.md).

## Decisão

A equipe escolheu a **Opção 1 — Cálculo de Benefícios como motor sem estado; Processamento de
Pagamentos é o dono único de escrita de `PAGAMENTO`**.

**Justificativa:** elimina a dupla fonte da verdade do cálculo (MYS-023) ao tornar o cálculo uma
função pura que retorna valores apurados, deixando uma única fronteira responsável por persistir o
ciclo de vida do pagamento — o que dá a fronteira mais limpa e o caminho mais testável entre as
alternativas avaliadas.

## Opções Consideradas

### Opção 1: Cálculo como motor sem estado; Pagamentos é dono único de escrita

- **Descrição:** O contexto de Cálculo de Benefícios expõe funções puras (`calcularBeneficio`,
  `calcularDescontos`, `corrigirIPCA`) que **retornam** valores apurados sem tocar em `PAGAMENTO`. O
  contexto de Processamento de Pagamentos chama o motor in-process e é o **único** a executar
  `STORE`/`UPDATE` em `PAGAMENTO`.
- **Prós:**
  - Elimina a dupla fonte da verdade do cálculo do benefício (resolve a raiz de MYS-023); só existe
    uma implementação da fórmula, consumida por folha mensal e por recálculos.
  - Fronteira de dados limpa: um único dono de escrita de `PAGAMENTO` casa com o recorte de
    [`bounded-contexts.md`](../bounded-contexts.md).
  - Cálculo torna-se testável de forma isolada (sem banco), facilitando testes de equivalência
    contra o legado para REQ-019/REQ-021/REQ-022/REQ-023.
- **Contras:**
  - O contexto de Pagamentos passa a orquestrar o ciclo de vida `G→P/D/E` e a persistência, ficando
    mais robusto/complexo.
  - Exige refactor em relação ao legado, onde os próprios programas de cálculo gravavam.
- **Risco:** uma orquestração mal projetada em Pagamentos poderia reespalhar lógica de cálculo;
  mitigável mantendo o motor como única fonte das fórmulas.
- **Esforço:** higher (relativo às demais opções).

### Opção 2: Cálculo persiste diretamente em `PAGAMENTO`

- **Descrição:** Cada serviço de cálculo (`CALCBENF`/`CALCCORR`/`CALCDSCT` equivalentes) grava
  diretamente em `PAGAMENTO`, espelhando o comportamento legado.
- **Prós:**
  - Menor distância do código legado; migração inicial mais direta.
  - Menos refactor de curto prazo.
- **Contras:**
  - Mantém **dois donos de escrita** de `PAGAMENTO` (Cálculo e Pagamentos), perpetuando o
    acoplamento que motivou o recorte.
  - Não resolve MYS-023: a divergência de cálculo entre fluxos continua possível.
- **Risco:** **alto** — cálculos podem divergir em produção (problema já existente no legado), com
  impacto financeiro direto nos benefícios pagos.
- **Esforço:** lower.

### Opção 3: `PAGAMENTO` como shared kernel

- **Descrição:** Um módulo de persistência compartilhado é dono do schema de `PAGAMENTO`; tanto
  Cálculo quanto Pagamentos escrevem através dele.
- **Prós:**
  - Centraliza o schema e o acesso a `PAGAMENTO` num único ponto.
- **Contras:**
  - Reintroduz acoplamento por dado compartilhado entre dois contextos — fronteira fraca, contrária
    ao objetivo do Modular Monolith.
  - Tende a virar um "god module" de difícil evolução.
- **Risco:** erosão das fronteiras de contexto ao longo do tempo; mudanças de schema viram pontos de
  contenção entre times.
- **Esforço:** same.

## Consequências

### Positivas

- Uma única implementação das fórmulas de benefício/desconto/correção, removendo a duplicação de
  MYS-023 e habilitando testes de equivalência confiáveis.
- `PAGAMENTO` tem dono de escrita único (Processamento de Pagamentos), reforçando a fronteira de
  bounded context e simplificando regras de consistência transacional.
- O motor de Cálculo, sem estado, pode ser exercitado isoladamente e reusado por folha mensal,
  recálculo e simulações sem efeitos colaterais de persistência.

### Negativas

- O contexto de Pagamentos absorve a responsabilidade de orquestração e persistência do ciclo
  `G→P/D/E`, aumentando sua complexidade interna.
- Maior esforço de migração frente ao legado, onde os programas de cálculo gravavam diretamente —
  exige cuidado para não reintroduzir lógica de cálculo dentro de Pagamentos.
- A correção monetária (`CALCCORR` → REQ-020), que no legado fazia `UPDATE` em `PAGAMENTO`, precisa
  ser remodelada: o motor calcula a correção e Pagamentos aplica a atualização.

## Requisitos Relacionados

- **Cálculo (motor sem estado):** REQ-018, REQ-019, REQ-020, REQ-021, REQ-022, REQ-023, REQ-024
- **Pagamentos (dono de escrita de `PAGAMENTO`):** REQ-025, REQ-026, REQ-027, REQ-028, REQ-029
- **Mistério relacionado:** MYS-023 (reimplementação inline do cálculo em `BATCHPGT`) — esta decisão
  endereça sua causa-raiz arquitetural; a definição da fórmula correta permanece bloqueador em
  Open Questions (OQ-002/OQ-003/OQ-004).

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="../bounded-contexts.md"><strong>bounded-contexts.md</strong></a><br/>
<sub>Os 5 contextos.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="../SPECIFICATION.md"><strong>SPECIFICATION.md</strong></a><br/>
<sub>Requisitos EARS.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../../README.md">Voltar ao Kit PT-BR</a></sub>
