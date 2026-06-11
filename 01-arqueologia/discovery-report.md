<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Relatório de Descoberta — Estágio 1: Arqueologia Digital

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **discovery-report**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Documento de passagem para o `@architect-agent` (Estágio 2). Autocontido — não exige abrir os artefatos individuais.

**Time**: _[preencher nome da equipe]_
**Data**: 2026-06-10
**Status das entradas**: ✅ [inventory.md](inventory.md) · ✅ [business-rules-catalog.md](business-rules-catalog.md) · ✅ [dependency-map.md](dependency-map.md) · ✅ [mysteries-found.md](mysteries-found.md)

---

## Resumo Executivo (máximo de 5 frases)

1. O legado SIFAP tem **15 programas Natural (`.NSN`) e 4 DDMs Adabas** (`BENEFICIARIO`, `PROGRAMA-SOCIAL`, `PAGAMENTO`, `AUDITORIA`), dos quais foram extraídas **125 regras de negócio candidatas** ([inventory.md](inventory.md); [business-rules-catalog.md](business-rules-catalog.md)).
2. Dessas, **~29 regras estão totalmente confirmadas** por documentação legada (RN-xxx / Manual), além de ~8 parciais; as demais são inferidas só do código ([business-rules-catalog.md](business-rules-catalog.md)).
3. O sistema é **fortemente acoplado por dados** (todos os programas giram em torno dos 4 DDMs) e contém **duplicação perigosa de lógica** — o batch `BATCHPGT` reimplementa o cálculo de `CALCBENF` inline em vez de chamá-lo via CALLNAT ([mysteries-found.md, MYS-023](mysteries-found.md)).
4. O maior risco para o Estágio 2 são os **9 mistérios bloqueadores (Critical)** (de **31 mistérios** catalogados no total), com destaque para fórmulas de cálculo divergentes da regra documentada (MYS-010, MYS-013) e três backdoors de segurança (MYS-017, MYS-019, MYS-021) ([mysteries-found.md](mysteries-found.md)).
5. Confiança da equipe para a modernização: **MÉDIA** — o domínio está bem mapeado e os status/fluxos foram decifrados, mas decisões financeiras e de segurança/compliance precisam ser resolvidas com facilitador antes de escrever EARS.

---

## O Que Sabemos (Confirmado)

### Regras de Negócio (somente confirmadas)

Extraídas de [business-rules-catalog.md](business-rules-catalog.md); somente itens classificados **"Confirmada"** (não inferidos). Agrupadas por tema.

**Validação de pessoa / documentos**

- CPF obrigatório e validado por dígito verificador (módulo 11, pesos 10→2 e 11→2) — _Unwanted/Ubiquitous_. [Ver business-rules-catalog.md, CADBENEF #2, #3, #4](business-rules-catalog.md); replicado em VALBENEF #1 e VALDOCS #1.
- Data de nascimento obrigatória — _Unwanted_. [Ver business-rules-catalog.md, CADBENEF #6](business-rules-catalog.md).
- Nome deve conter ao menos um espaço (nome + sobrenome) — _Unwanted_. [Ver business-rules-catalog.md, VALBENEF #5](business-rules-catalog.md).
- Domínio fechado de status do beneficiário **A=ativo, S=suspenso, C=cancelado, I=inativo, D=desligado** — _Unwanted_. [Ver business-rules-catalog.md, VALBENEF #7](business-rules-catalog.md); rótulos confirmados em CONSBENF #4 e VALELEG #5.

**Cadastro e ciclo de vida**

- Beneficiário incluído recebe status inicial `A` (ativo) — _Event-driven_. [Ver business-rules-catalog.md, CADBENEF #10](business-rules-catalog.md).
- Programa incluído recebe status `A` (ativo) — _Event-driven_. [Ver business-rules-catalog.md, CADPROG #5](business-rules-catalog.md).
- Inclusão de dependente exige titular existente (chave CPF) — _Unwanted_. [Ver business-rules-catalog.md, CADDEPEND #1](business-rules-catalog.md).

**Elegibilidade**

- Apenas programas com status `A` são elegíveis — _Unwanted_. [Ver business-rules-catalog.md, VALELEG #3](business-rules-catalog.md).
- Faixa etária por programa (idade mínima/máxima quando definidas) — _State-driven_. [Ver business-rules-catalog.md, VALELEG #6](business-rules-catalog.md).
- Teto de renda por programa (`RENDA-MAX`) — _State-driven_. [Ver business-rules-catalog.md, VALELEG #7](business-rules-catalog.md).
- Apenas beneficiários com status `A` entram no cálculo / pagamento — _Unwanted/State-driven_. [Ver business-rules-catalog.md, CALCBENF #3 e BATCHPGT #3](business-rules-catalog.md).

**Cálculo financeiro**

- Valores monetários são **truncados** (não arredondados) para 2 casas decimais — _Ubiquitous_. [Ver business-rules-catalog.md, CALCBENF #11 e CALCCORR #6](business-rules-catalog.md).
- Teto de desconto = **30% do valor bruto**, com desconto judicial (`J`) como exceção legal ao teto — _Ubiquitous/Event-driven/State-driven_. [Ver business-rules-catalog.md, CALCDSCT #4, #6, #12](business-rules-catalog.md).

**Pagamento / conciliação / auditoria**

- Processamento mensal lê beneficiários **em ordem de CPF** (sistemas a jusante dependem disso) — _Ubiquitous_. [Ver business-rules-catalog.md, BATCHPGT #1](business-rules-catalog.md).
- Conciliação CNAB: divergência quando |valor SIFAP − valor banco| > R$ 0,01; código de retorno define status **`00`→P (pago), `01`→D (devolvido), `02`→E (estornado)** — _Unwanted/Event-driven_. [Ver business-rules-catalog.md, BATCHCON #3, #4](business-rules-catalog.md).
- Domínio de status de pagamento **G=gerado, P=pago, C=cancelado, D=devolvido, E=estornado** — _Ubiquitous_. [Ver business-rules-catalog.md, BATCHREL #3 e RELPGT #6](business-rules-catalog.md).
- Domínio de ações de auditoria **IN, AL, CO, CN, DV** (e `EX` — ver risco) — _Ubiquitous_. [Ver business-rules-catalog.md, RELAUDIT #5](business-rules-catalog.md).
- Registro de auditoria gravado para cada conciliação e divergência — _Event-driven_. [Ver business-rules-catalog.md, BATCHCON #5](business-rules-catalog.md).

> **Contagem:** ~29 regras totalmente confirmadas (+ ~8 parciais). Total geral de candidatas: 125. [Ver Resumo Estatístico em business-rules-catalog.md](business-rules-catalog.md).

### Dependências (arestas verificadas)

> ✅ [dependency-map.md](dependency-map.md) **foi preenchido** com arestas reais (escopo: `natural-programs/`, 15 programas + 4 DDMs, recursivo). Cada aresta cita arquivo:linha. Diagrama Mermaid em [dependency-map.mmd](dependency-map.mmd).

**Achado central:** **nenhum `CALLNAT` nem `INCLUDE`** nos 15 programas — logo **0 arestas programa→programa**. Toda integração é **implícita via DDMs compartilhados** (acoplamento por dados). [Ver dependency-map.md, "Arestas Programa-para-Programa"](dependency-map.md).

**Acoplamento programa → DDM (34 arestas de dados verificadas, com arquivo:linha):**

- `BENEFICIARIO` — DDM mais acessado, **9 programas**: CADBENEF, CADDEPEND, CALCBENF, CALCDSCT, VALELEG, CONSBENF, RELPGT, BATCHREL, BATCHPGT. [Ver dependency-map.md](dependency-map.md).
- `PAGAMENTO` — **8 programas**, concentra as escritas (STORE/UPDATE por CALCBENF, CALCCORR, CALCDSCT, BATCHPGT, BATCHCON). [Ver dependency-map.md](dependency-map.md).
- `PROGRAMA-SOCIAL` — CADPROG, VALELEG, CALCBENF, BATCHPGT. [Ver dependency-map.md](dependency-map.md).
- `AUDITORIA` — escrito por BATCHCON; lido por RELAUDIT. [Ver dependency-map.md](dependency-map.md).

**Arestas program → program / referências quebradas:**

- `BATCHPGT` **deveria** chamar `CALCBENF`/`CALCDSCT` (CALLNAT prometido no cabeçalho), mas **reimplementa a lógica inline** — chamada esperada **ausente**. [Ver mysteries-found.md, MYS-023](mysteries-found.md).
- **Programas órfãos:** `VALBENEF` e `VALDOCS` — sem acesso a dados e ninguém os chama (MYS-030). [Ver dependency-map.md, "Programas Órfãos"](dependency-map.md).
- **Subprogramas ausentes:** `VALCPF`, `VALNISN`, copycode `FMTVLR` — citados na doc, não materializados (MYS-031). [Ver inventory.md, "Itens Incomuns"](inventory.md).
- **Fonte externa:** `BATCHCON` lê arquivo CNAB 240 (`WORK FILE 1`, não-DDM). [Ver dependency-map.md](dependency-map.md).

### Estruturas de Dados (DDMs documentados)

Campos-chave extraídos dos blocos `DEFINE DATA` em [business-rules-catalog.md](business-rules-catalog.md); existência dos 4 `.ddm` confirmada em [inventory.md](inventory.md).

- **`BENEFICIARIO`** — CPF (N11, chave), NOME, DT-NASCIMENTO, SEXO, STATUS (A/S/C/I/D), COD-PROGRAMA, RENDA-FAMILIAR, NUM-DEPENDENTES, COD-REGIAO, NIS, UF, CEP, DOCUMENTOS-OK; grupos periódicos **`DEPENDENTES (PE)`** e **`DESCONTOS (PE)`** (TIPO-DSCT C/I/J/S/P/A).
- **`PROGRAMA-SOCIAL`** — COD-PROGRAMA (N4, chave), NOME-PROGRAMA, TIPO (A/P/T), VLR-BASE, COD-ELEGIBILIDADE (A5 posicional), DT-INICIO/DT-FIM, STATUS-PROG, RENDA-MAX, IDADE-MIN/MAX, FATOR-REAJUSTE.
- **`PAGAMENTO`** — NUM-PAGTO, CPF-BENEF, COMPETENCIA, VLR-BRUTO/DESCONTO/LIQUIDO, TIPO-PGTO (N/D/T), STATUS-PGTO (G/P/C/D/E), VLR-CORRECAO, COD-BANCO, COD-RETORNO.
- **`AUDITORIA`** — SEQ-AUDIT, DT-EVENTO, USUARIO, ACAO (IN/AL/CO/CN/DV/EX), TABELA-REF, CHAVE-REF, VLR-ANTERIOR/NOVO.

---

## O Que Traz Risco

### Mistérios que Bloqueiam o Estágio 2

**9 mistérios `blocks-stage-2` (Critical)** — devem ser resolvidos com facilitador/domínio antes de qualquer EARS dependente. [Fonte: mysteries-found.md](mysteries-found.md).

| MYS-ID | Risco | Caminho de resolução sugerido |
| ------ | ----- | ----------------------------- |
| **MYS-001** | Fator-K mágico `0.347215` adultera `VLR-BASE` na inclusão de programa (CADPROG) — base de todo o cálculo. | Buscar `0.347215` em CALCBENF/CALCCORR; cruzar com REGRAS-NEGOCIO-2012 §6. |
| **MYS-010** | Cálculo de benefício **MULTIPLICATIVO de 5 fatores** diverge da RN-013 **ADITIVA** (CALCBENF). | Decisão de domínio: qual fórmula é a oficial? Validar com folha de pagamento real. |
| **MYS-012** | Fórmula do 13º: comentário (× meses ativos/12) ≠ código (× fator idade) (CALCBENF/BATCHPGT). | Confirmar fórmula correta do 13º com facilitador. |
| **MYS-013** | **Três cálculos de desconto divergentes** (3% fixo em CALCBENF/BATCHPGT vs progressivo 3/5/7/9% em CALCDSCT, não chamado). | Decidir a fonte da verdade; verificar se há desconto a menor. |
| **MYS-016** | Pensão alimentícia (`P`) submetida ao teto de 30% (só `J` é exceção) — possível defeito jurídico (CALCDSCT). | Questão jurídica: pensão deve ser exceção ao teto? |
| **MYS-017** | **Bypass de elegibilidade da região 99** — ESCAPE ROUTINE pula TODA validação (VALELEG). | Decisão de segurança: replicar com controle de acesso ou eliminar. |
| **MYS-019** | **Backdoor de CPF de teste** — CPFs `000…` com dígitos iguais aceitos em produção (VALBENEF). | Decisão de segurança: remover backdoor. |
| **MYS-021** | **Backdoor de prefixos especiais** {000,001,002,010,011,099,100,999} anula validação documental (VALDOCS). | Decisão de segurança: eliminar bypass. |
| **MYS-029** | **Exclusões (`EX`) ocultadas da trilha de auditoria** (RELAUDIT) — bandeira vermelha de compliance. | Trilha modernizada DEVE exibir exclusões; verificar se `EX` é ao menos gravado. |

> Demais: **12 High** (needs-investigation), **5 Medium** (needs-facilitator), **5 Low** (parked, 2 ✅ resolvidos). Total **31** mistérios. [Ver resumo em mysteries-found.md](mysteries-found.md).

### Regras com Evidência Fraca

Regras classificadas **"Inferida"** carregam risco se viradas em requisito sem confirmação — derivam só do código, sem suporte documental. [Fonte: business-rules-catalog.md](business-rules-catalog.md). Destaques:

- **Limite de dependentes = 5 no código vs 3 na RN-004** — divergência confirmada, magic number hardcoded. [CADDEPEND #3](business-rules-catalog.md).
- **Cálculo de idade só por diferença de anos** (ignora mês/dia) — afeta regra dos 75 anos e elegibilidade. [CADBENEF #12](business-rules-catalog.md), [VALELEG #6](business-rules-catalog.md).
- **Tabelas de fatores (regional/renda/idade) e alíquotas** são magic numbers não documentados. [CALCBENF #5–#8](business-rules-catalog.md), [CALCDSCT #3](business-rules-catalog.md).
- **Correção IPCA só cobre 2010–2012** — anos ausentes ficam sem correção, silenciosamente. [CALCCORR #4–#5](business-rules-catalog.md).
- **Reimplementação inline do cálculo no batch** — dupla fonte da verdade. [BATCHPGT #6](business-rules-catalog.md).

> **Risco residual de dependência:** o [dependency-map.md](dependency-map.md) confirmou **2 mistérios novos** — validadores órfãos `VALBENEF`/`VALDOCS` (MYS-030) e subprogramas ausentes `VALCPF`/`VALNISN`/`FMTVLR` (MYS-031). A validação de NIS (RN-001) não ocorre em nenhum programa materializado.

---

## Hipóteses de Recorte Recomendadas

> ⚠️ **São HIPÓTESES, não decisões.** Baseiam-se em clusters de famílias de prefixo ([inventory.md](inventory.md)) e em propriedade de dados / acoplamento por DDM verificado em [dependency-map.md](dependency-map.md). O `@architect-agent` decide no Estágio 2.

### Hipótese 1: Cadastro de Beneficiários — registro e validação de pessoas físicas e seus dependentes/documentos
- **Programas:** CADBENEF, CADDEPEND, VALBENEF, VALDOCS
- **DDM(s) próprios:** `BENEFICIARIO` (incl. grupo periódico `DEPENDENTES`)
- **Racional:** Fronteira natural em torno do dado da pessoa e suas validações (CPF, nome, data, documentos).

### Hipótese 2: Programas Sociais e Elegibilidade — definição de programas e regras de quem pode receber
- **Programas:** CADPROG, VALELEG
- **DDM(s) próprios:** `PROGRAMA-SOCIAL`
- **Racional:** O programa define os parâmetros (renda, idade, tipo, COD-ELEGIBILIDADE) que VALELEG consome — coesão de regra de elegibilidade.

### Hipótese 3: Cálculo de Benefícios — motor financeiro de valores, descontos e correções
- **Programas:** CALCBENF, CALCDSCT, CALCCORR
- **DDM(s) próprios:** escreve em `PAGAMENTO` (lê `BENEFICIARIO`, `PROGRAMA-SOCIAL`)
- **Racional:** Concentra a lógica financeira sensível (fatores, teto de 30%, IPCA) — candidato a serviço único de cálculo que elimina a duplicação do batch.

### Hipótese 4: Processamento de Pagamentos — ciclo mensal e conciliação bancária (PAGAMENTO)
- **Programas:** BATCHPGT, BATCHCON
- **DDM(s) próprios:** `PAGAMENTO` (ciclo de vida G→P/D/E)
- **Racional:** Orquestração batch da folha + conciliação CNAB; deveria consumir o motor de cálculo (Hipótese 3) em vez de reimplementá-lo.

### Hipótese 5: Consultas, Relatórios e Auditoria — visões somente-leitura e trilha de auditoria
- **Programas:** CONSBENF, RELPGT, BATCHREL, RELAUDIT
- **DDM(s) próprios:** `AUDITORIA` (leitura cruzada de `PAGAMENTO`/`BENEFICIARIO`)
- **Racional:** Separa preocupações de leitura/relatório (mascaramento de CPF, consolidações, trilha) do caminho de escrita.

---

## Artefatos-Fonte

- [inventory.md](inventory.md) — inventário do legado (estrutura, contagens, convenções de nomes).
- [business-rules-catalog.md](business-rules-catalog.md) — 125 regras candidatas por programa, com rastreabilidade `.NSN#L`.
- [dependency-map.md](dependency-map.md) · [dependency-map.mmd](dependency-map.mmd) — 34 arestas programa→dados verificadas; 0 CALLNAT/INCLUDE.
- [mysteries-found.md](mysteries-found.md) — 31 mistérios consolidados (9 bloqueadores).
- Legado: [legado-sifap/](legado-sifap/) — 15 `.NSN`, 4 `.ddm`, docs em `legacy-docs/`.

---

## Aprovação da Equipe

> Preencher na conversa guiada de passagem para o Estágio 2.

- **Reviewed by:** _________________________ (nomes)
- **Date:** _______________
- **Confidence:** ☐ high ☐ medium ☐ low
- **Pendências antes do gate:** ☐ encaminhar os 9 bloqueadores ao facilitador · ☐ investigar órfãos/subprogramas ausentes (MYS-030, MYS-031)


---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="mysteries-found.md"><strong>mysteries-found.md</strong></a><br/>
<sub>Lista de mistérios.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="../02-spec-moderna/GUIDE.md"><strong>Estágio 2 — Spec</strong></a><br/>
<sub>Próximo estágio: spec moderna.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../README.md">Voltar ao Kit PT-BR</a></sub>

