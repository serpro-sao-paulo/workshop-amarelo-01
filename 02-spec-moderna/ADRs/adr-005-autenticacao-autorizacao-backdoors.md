<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# ADR-005: Autenticação/Autorização e Tratamento dos Backdoors Legados

![ESTÁGIO 02 Spec Moderna](https://img.shields.io/badge/ESTÁGIO-02%20Spec%20Moderna-00A4EF?style=for-the-badge) ![ADR MADR](https://img.shields.io/badge/ADR-MADR-1A1A1A?style=for-the-badge) ![STATUS Aceita](https://img.shields.io/badge/STATUS-Aceita-2E7D32?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../../README.md) → [Estágio 2](../README.md) → **ADRs** → **ADR-005**

> Gerado por `/generate-adr` em 2026-06-10. Decisão ratificada pela equipe.

## Status

Aceita

## Data

2026-06-10

## Contexto

O sistema modernizado expõe uma REST API (REQ-033) e precisa de autenticação/autorização — a stack-alvo
define **OAuth2/JWT com Spring Security**. Em paralelo, o legado contém **três comportamentos de
"backdoor"** hoje classificados como **bloqueadores de segurança** em
[`mysteries-found.md`](../../01-arqueologia/mysteries-found.md) e listados em Open Questions de
[`SPECIFICATION.md`](../SPECIFICATION.md):

- **MYS-017 / OQ-006** — `COD-REGIAO = 99` concede elegibilidade automática, pulando toda a validação
  (rotulado "INTERNACIONAL/DIPLOMATICO").
- **MYS-019 / OQ-007** — CPFs com dígitos iguais iniciados em `000` aceitos como "TESTE GOVERNO".
- **MYS-021 / OQ-008** — prefixos de CPF {000,001,002,010,011,099,100,999} anulam toda a validação
  documental.

Precisamos decidir a estratégia de auth **e** o destino desses comportamentos, porque ambos moldam o
modelo de segurança da aplicação e as regras de elegibilidade/validação (REQ-014–017, REQ-001–003).

> ⚠️ **Nota de segurança:** os três comportamentos são, na prática legada, bypasses de controle. A
> opção escolhida (replicar sob controle de acesso) só é defensável se forem convertidos em
> **exceções explícitas, autorizadas por papel (role) e auditadas** — nunca como bypass silencioso.

## Decisão

A equipe escolheu **OAuth2/JWT (Spring Security) e replicar os comportamentos legados como exceções
explícitas sob controle de acesso por papel (role)**, em vez de eliminá-los.

**Justificativa:** há casos de negócio legítimos por trás de alguns desses comportamentos (ex.: regime
diplomático/internacional para a região 99; CPFs de teste/governo), e removê-los cegamente poderia
quebrar fluxos reais; a equipe opta por preservá-los **somente** como operações privilegiadas,
autenticadas, autorizadas por papel específico e registradas na trilha de auditoria.

## Opções Consideradas

### Opção 1: OAuth2/JWT + eliminar os backdoors

- **Descrição:** autenticação OAuth2/JWT padrão; região 99, CPF `000` e prefixos especiais são
  removidos do fluxo de produção e tratados como dados inválidos comuns.
- **Prós:**
  - Postura de segurança mais simples; elimina vetores de fraude conhecidos (OWASP — Broken Access
    Control).
  - Regras de elegibilidade/validação ficam uniformes, sem exceções.
- **Contras:**
  - Pode quebrar casos legítimos que dependiam do comportamento (ex.: diplomáticos, CPFs de teste
    institucionais), se existirem.
- **Risco:** baixo de segurança; médio de regressão funcional se os casos forem reais e não mapeados.
- **Esforço:** lower.

### Opção 2: OAuth2/JWT + replicar sob controle de acesso/role explícita — escolhida

- **Descrição:** autenticação OAuth2/JWT; os comportamentos legados viram **exceções explícitas**,
  liberadas apenas para papéis específicos (ex.: `ROLE_DIPLOMATICO`, `ROLE_TESTE`), sempre auditadas.
- **Prós:**
  - Preserva casos de negócio legítimos sem manter bypass silencioso.
  - Converte um vetor de fraude em operação privilegiada, autenticada e rastreável.
  - Compatível com a centralização de auditoria (ADR-003) para registrar cada uso da exceção.
- **Contras:**
  - Exige modelagem cuidadosa de papéis e regras de autorização; risco se mal configurado.
  - Mantém na superfície um comportamento sensível que precisa de governança contínua.
- **Risco:** **médio/alto** se as roles/escopos forem mal definidos — um erro de configuração recria o
  bypass. Mitigação obrigatória: negar por padrão, exigir role explícita, auditar todo uso e cobrir
  com testes de autorização.
- **Esforço:** higher.

### Opção 3: OAuth2/JWT apenas; backdoors em ADR separado depois

- **Descrição:** decide só a auth agora; adia a decisão sobre os backdoors.
- **Prós:**
  - Destrava a auth sem travar nas questões de domínio/segurança em aberto.
- **Contras:**
  - Deixa REQ-014–017 e REQ-001–003 com ambiguidade de segurança pendente; risco de implementar antes
    de decidir.
- **Risco:** médio — decisão crítica adiada.
- **Esforço:** same.

## Consequências

### Positivas

- API protegida por OAuth2/JWT com Spring Security, alinhada à stack-alvo e às regras OWASP (autenticação
  e autorização explícitas).
- Casos legítimos (diplomático/teste) preservados como operações privilegiadas, não como bypass.
- Todo uso de exceção é auditável via a porta de auditoria (ADR-003), criando rastro de compliance.

### Negativas

- Superfície de segurança sensível permanece e exige **governança contínua**: revisão de papéis,
  negação por padrão e testes de autorização dedicados.
- Configuração incorreta de roles/escopos pode **recriar o vetor de fraude** — risco residual real que
  precisa de testes de segurança e revisão entre pares.
- As Open Questions OQ-006/007/008 deixam de ser "eliminar vs manter" e passam a exigir a
  **especificação precisa de cada exceção** (qual papel, qual escopo, qual auditoria) antes da
  implementação.

## Requisitos Relacionados

- **API/segurança:** REQ-033 (REST API)
- **Elegibilidade afetada:** REQ-014, REQ-015, REQ-016, REQ-017 (exceção região 99)
- **Validação de CPF afetada:** REQ-001, REQ-002, REQ-003 (exceções `000`/prefixos especiais)
- **Auditoria das exceções:** REQ-029, REQ-032 (via ADR-003)
- **Mistérios/Open Questions:** MYS-017/OQ-006, MYS-019/OQ-007, MYS-021/OQ-008 — esta decisão define a
  **direção** (replicar sob role); a especificação por exceção permanece a resolver nas Open Questions.

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="adr-004-comunicacao-inter-context.md"><strong>ADR-004</strong></a><br/>
<sub>Comunicação inter-context.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="../SPECIFICATION.md"><strong>SPECIFICATION.md</strong></a><br/>
<sub>Requisitos EARS.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../../README.md">Voltar ao Kit PT-BR</a></sub>
