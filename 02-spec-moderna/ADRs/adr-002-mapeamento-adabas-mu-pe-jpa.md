<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# ADR-002: Mapeamento de Campos Adabas MU/PE para JPA/PostgreSQL

![ESTÁGIO 02 Spec Moderna](https://img.shields.io/badge/ESTÁGIO-02%20Spec%20Moderna-00A4EF?style=for-the-badge) ![ADR MADR](https://img.shields.io/badge/ADR-MADR-1A1A1A?style=for-the-badge) ![STATUS Aceita](https://img.shields.io/badge/STATUS-Aceita-2E7D32?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../../README.md) → [Estágio 2](../README.md) → **ADRs** → **ADR-002**

> Gerado por `/generate-adr` em 2026-06-10. Decisão ratificada pela equipe.

## Status

Aceita

## Data

2026-06-10

## Contexto

O modelo de dados Adabas do SIFAP usa estruturas que não têm equivalente direto no modelo relacional:

- **Grupo periódico (PE) `DEPENDENTES`** em `BENEFICIARIO` — cada dependente tem NOME-DEP, DT-NASC-DEP,
  PARENTESCO, CPF-DEP, DOC-DEP, SEXO-DEP ([`business-rules-catalog.md`, CADDEPEND](../../01-arqueologia/business-rules-catalog.md)).
- **Grupo periódico (PE) `DESCONTOS`** em `BENEFICIARIO` — cada desconto tem TIPO-DSCT, VLR-DSCT,
  PCT-DSCT, DT-INICIO/FIM-DSCT, NUM-PROCESSO ([`business-rules-catalog.md`, CALCDSCT](../../01-arqueologia/business-rules-catalog.md)).

Ambos os PEs carregam **regras de negócio** sobre seus elementos: limite de dependentes (REQ-010),
domínio de parentesco (REQ-011), e descontos com vigência por janela de datas, tipo e teto de 30%
(REQ-021 a REQ-023). Precisamos decidir como persistir essas coleções no PostgreSQL 16 com JPA/Hibernate
antes de produzir o data-model, pois a escolha afeta consultas, indexação e a expressividade das regras.

## Decisão

A equipe escolheu **`@OneToMany` em tabela própria** — cada grupo periódico vira uma entidade filha
com chave própria e relacionamento gerenciado a partir do agregado `Beneficiario`.

**Justificativa:** tanto `DEPENDENTES` quanto `DESCONTOS` são entidades ricas com regras, vigência e
consultas próprias; modelá-las como entidades filhas relacionais dá identidade, integridade
referencial e consultabilidade que as regras (REQ-010/011, REQ-021–023) exigem.

## Opções Consideradas

### Opção 1: `@OneToMany` em tabela própria

- **Descrição:** `Dependente` e `Desconto` são entidades JPA com PK própria e FK para `Beneficiario`,
  mapeadas via `@OneToMany`/`@ManyToOne`.
- **Prós:**
  - Cada elemento tem identidade e pode ser consultado/indexado isoladamente (ex.: descontos vigentes,
    contagem de dependentes para REQ-010).
  - Regras de domínio (parentesco REQ-011, vigência e teto REQ-021–023) ficam naturais em entidades.
  - Integridade referencial e migrações relacionais previsíveis.
- **Contras:**
  - Mais tabelas e joins; mapeamento mais verboso que coleções simples.
- **Risco:** baixo; padrão bem suportado por Hibernate.
- **Esforço:** higher (mais entidades/migrações).

### Opção 2: `@ElementCollection`

- **Descrição:** os PEs viram coleções de objetos embutidos em uma tabela secundária gerenciada pelo JPA.
- **Prós:**
  - Menos boilerplate para coleções "de valor" sem identidade própria.
- **Contras:**
  - Elementos não têm identidade estável; atualizações parciais e consultas por elemento são
    limitadas — atrito com as regras de vigência/teto dos descontos.
- **Risco:** médio; refatorar para entidade depois é custoso.
- **Esforço:** same.

### Opção 3: Coluna JSONB

- **Descrição:** os PEs são serializados como documento aninhado em uma coluna `jsonb`.
- **Prós:**
  - Leitura do agregado em uma só linha; flexível para estruturas que mudam.
- **Contras:**
  - Consultas/índices por elemento exigem operadores JSONB; regras relacionais (FK, unicidade de
    CPF-DEP) ficam fora do banco; validação migra toda para a aplicação.
- **Risco:** médio/alto para dados com regras e relatórios (RELPGT/CONSBENF consultam histórico).
- **Esforço:** lower inicialmente, higher na evolução.

### Opção 4: Híbrido (OneToMany p/ DEPENDENTES, JSONB p/ DESCONTOS)

- **Descrição:** mistura conforme a natureza de cada PE.
- **Prós:**
  - Otimiza caso a caso.
- **Contras:**
  - Dois padrões no mesmo agregado aumentam a carga cognitiva; descontos são justamente os que mais
    têm regras (vigência, teto, tipo) e se beneficiariam de tabela própria.
- **Risco:** inconsistência de abordagem.
- **Esforço:** same.

## Consequências

### Positivas

- Dependentes e descontos ganham identidade, integridade referencial e consultabilidade — alinhados
  às regras REQ-010/011 e REQ-021–023.
- Relatórios e consultas (CONSBENF/RELPGT) acessam o histórico via joins relacionais padrão.
- Caminho de indexação claro (ex.: índice por `cpf_beneficiario` + vigência de desconto).

### Negativas

- Mais tabelas, mapeamentos e migrações para manter.
- Carregamento do agregado exige cuidado com fetch (evitar N+1) — definir `fetch`/consultas explícitas.

## Requisitos Relacionados

- **DEPENDENTES:** REQ-009, REQ-010, REQ-011
- **DESCONTOS:** REQ-021, REQ-022, REQ-023
- **Consulta/relatório do histórico:** REQ-030, REQ-031

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="adr-001-ownership-escrita-pagamento.md"><strong>ADR-001</strong></a><br/>
<sub>Ownership de PAGAMENTO.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="adr-003-caminho-escrita-auditoria.md"><strong>ADR-003</strong></a><br/>
<sub>Escrita de auditoria.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../../README.md">Voltar ao Kit PT-BR</a></sub>
