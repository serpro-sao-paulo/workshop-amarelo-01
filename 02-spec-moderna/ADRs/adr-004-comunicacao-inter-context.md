<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# ADR-004: Mecanismo de Comunicação Entre Bounded Contexts

![ESTÁGIO 02 Spec Moderna](https://img.shields.io/badge/ESTÁGIO-02%20Spec%20Moderna-00A4EF?style=for-the-badge) ![ADR MADR](https://img.shields.io/badge/ADR-MADR-1A1A1A?style=for-the-badge) ![STATUS Aceita](https://img.shields.io/badge/STATUS-Aceita-2E7D32?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../../README.md) → [Estágio 2](../README.md) → **ADRs** → **ADR-004**

> Gerado por `/generate-adr` em 2026-06-10. Decisão ratificada pela equipe.

## Status

Aceita

## Data

2026-06-10

## Contexto

O sistema modernizado é um **Modular Monolith** (Java 21 + Spring Boot), com comunicação **in-process**
— sem HTTP entre módulos. O legado **não tem chamadas entre programas** (zero `CALLNAT`/`INCLUDE`,
confirmado em [`dependency-map.md`](../../01-arqueologia/dependency-map.md)); toda integração era por
DDMs compartilhados. Ao redesenhar, [`bounded-contexts.md`](../bounded-contexts.md) mapeou os fluxos
entre os 5 contextos (ex.: Pagamentos → Cálculo, Pagamentos → Auditoria, eventos `PagamentoGerado`/
`PagamentoConciliado`).

Precisamos decidir o mecanismo de comunicação inter-context antes dos contratos, pois ele molda o
acoplamento temporal, a consistência e a testabilidade. Decisões anteriores reforçam o tema: ADR-001
(Pagamentos chama o motor de Cálculo) e ADR-003 (Pagamentos registra auditoria por porta).

## Decisão

A equipe escolheu **domain events** como mecanismo primário de comunicação entre contextos
(publicação/assinatura in-process, ex.: via `ApplicationEventPublisher` do Spring), com
desacoplamento assíncrono entre o produtor e os consumidores do evento.

**Justificativa:** o desacoplamento assíncrono evita que um contexto dependa do tempo de execução do
outro e reflete bem o estilo legado (integração indireta, sem chamadas diretas), permitindo que
múltiplos consumidores (auditoria, relatórios, read models) reajam a fatos de negócio como
`PagamentoGerado`/`PagamentoConciliado` sem acoplar o produtor.

## Opções Consideradas

### Opção 1: Chamadas de método in-process via interface (síncrono)

- **Descrição:** cada contexto expõe interfaces (portas); os demais chamam diretamente, de forma
  síncrona, dentro da mesma transação quando aplicável.
- **Prós:**
  - Fluxo simples e rastreável; consistência transacional direta.
  - Ideal para **consultas** (ex.: Cálculo lendo `Beneficiario`/`Programa`).
- **Contras:**
  - Acopla o chamador ao tempo de resposta e à disponibilidade do chamado.
  - Múltiplos reagentes a um mesmo fato exigem o produtor conhecer todos.
- **Risco:** baixo, mas tende a acoplar fluxos de notificação.
- **Esforço:** lower.

### Opção 2: Domain events (assíncrono) — escolhida

- **Descrição:** contextos publicam eventos de domínio; consumidores assinam e reagem de forma
  desacoplada (in-process; assíncrono onde fizer sentido).
- **Prós:**
  - Produtor não conhece os consumidores; novos reagentes (auditoria, read models) entram sem mudar o
    produtor.
  - Reflete a integração indireta do legado; bom encaixe para `PagamentoGerado`/`PagamentoConciliado`.
  - Reduz acoplamento temporal entre contextos.
- **Contras:**
  - Consistência eventual: exige cuidado com ordem, idempotência e tratamento de falha de consumidor.
  - Depuração de fluxos orientados a evento é menos linear.
- **Risco:** médio — consistência eventual e entrega de eventos precisam de disciplina (outbox,
  idempotência).
- **Esforço:** higher.

### Opção 3: Híbrido (síncrono + eventos)

- **Descrição:** chamadas síncronas para queries/comandos que exigem resposta imediata e consistência;
  eventos para notificações e reações desacopladas.
- **Prós:**
  - Usa a ferramenta certa por caso (query síncrona, notificação por evento).
- **Contras:**
  - Dois modelos mentais coexistindo; exige convenção clara de quando usar cada um.
- **Risco:** médio — risco de inconsistência de estilo sem governança.
- **Esforço:** same.

## Consequências

### Positivas

- Baixo acoplamento entre produtores e consumidores de fatos de negócio; extensível sem alterar o
  produtor.
- Auditoria (ADR-003), relatórios e futuros read models reagem a eventos sem o produtor conhecê-los.
- Estilo coerente com a integração indireta observada no legado.

### Negativas

- Consistência eventual exige padrões de robustez: idempotência de consumidores, ordem de eventos e,
  possivelmente, **transactional outbox** para garantir publicação atômica com a mudança de estado.
- Fluxos puramente de **consulta** (ex.: Cálculo lê `Beneficiario`/`Programa`) não se encaixam em
  eventos e ainda exigirão chamadas síncronas — a equipe deve documentar essa exceção para não forçar
  tudo a evento.
- Observabilidade/depuração mais complexa que chamadas síncronas diretas.

## Requisitos Relacionados

- **Eventos de pagamento:** REQ-027, REQ-028, REQ-029 (produzem `PagamentoConciliado`/divergência)
- **Auditoria reativa:** REQ-029, REQ-032 (consome eventos via ADR-003)
- **Decisões relacionadas:** ADR-001 (orquestração de Pagamentos), ADR-003 (porta de auditoria)

> **Nota de implementação (a detalhar em ADR futuro):** definir o uso de **transactional outbox** e a
> fronteira entre comandos/queries síncronos e eventos assíncronos, já que esta decisão adota eventos
> como mecanismo primário mas as queries permanecem síncronas.

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="adr-003-caminho-escrita-auditoria.md"><strong>ADR-003</strong></a><br/>
<sub>Escrita de auditoria.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="adr-005-autenticacao-autorizacao-backdoors.md"><strong>ADR-005</strong></a><br/>
<sub>Auth + backdoors.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../../README.md">Voltar ao Kit PT-BR</a></sub>
