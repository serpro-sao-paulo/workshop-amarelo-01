<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Mistérios Encontrados — SIFAP Legado

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **mysteries-found**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Registre aqui toda lógica, comportamento ou código que o time não conseguiu explicar.
> "Mistérios" são trechos de código sem documentação, com lógica não-óbvia ou que parecem workarounds.
>
> **Cota mínima para passar pelo portão do Estágio 2:** 5 mistérios documentados.

## O que conta como "mistério"?

- Código que faz algo inesperado sem comentário explicando por quê
- Valores hardcoded sem explicação (números mágicos)
- Lógica condicional que parece um workaround ou gambiarra
- Campos no DDM que não são usados por nenhum programa
- Programas que existem mas não são chamados por ninguém
- Comportamento diferente entre o que a documentação diz e o que o código faz
- Easter eggs deixados pelos desenvolvedores originais

## Níveis de Confiança

| Nível     | Significado                                         |
| --------- | --------------------------------------------------- |
| **ALTA**  | Temos certeza de que há algo estranho aqui          |
| **MÉDIA** | Parece suspeito, mas pode ter explicação            |
| **BAIXA** | Pode ser intencional, mas não conseguimos confirmar |

## Mistérios Catalogados

> Gerado por `/catalog-mysteries` em 2026-06-10 (**re-execução** após o preenchimento de
> [`dependency-map.md`](dependency-map.md) com arestas reais). **Escopo:** todos os artefatos sob `01-arqueologia/`
> (varredura de marcadores `<!-- mystery: -->`). Os marcadores da equipe vivem em
> [`business-rules-catalog.md`](business-rules-catalog.md) (33 marcadores, rastreando para os programas-fonte `.NSN`
> em `legado-sifap/natural-programs/`) e agora também em [`dependency-map.md`](dependency-map.md) (2 marcadores novos,
> de nível de dependência).
> **Dedupção:** 35 marcadores `<!-- mystery: -->` foram consolidados em **31 mistérios únicos**
> (4 marcadores fundidos em 3 entradas de raiz comum — MYS-010, MYS-013 e MYS-027; +2 novos de dependência: MYS-030, MYS-031).
> Ordenado por severidade (Critical primeiro). Nenhum mistério foi resolvido por especulação;
> os marcados ✅ foram resolvidos por **evidência** encontrada em outro programa.
>
> **Resumo:** Total **31** · Bloqueadores (blocks-stage-2 / Critical) **9** · Investigação (needs-investigation / High) **12** · Facilitador (needs-facilitator / Medium) **5** · Estacionados (parked / Low) **5**.

| ID      | Descrição | Fonte | Classificação | Severidade | Ação sugerida |
| ------- | --------- | ----- | ------------- | ---------- | ------------- |
| MYS-001 | **Fator-K `0.347215`** — na inclusão de programa social o sistema grava `VLR-BASE × (1.00 + FATOR-REAJUSTE × 0.347215)` em VLR-BASE, em vez do valor informado. Número mágico sem documentação; adultera o valor-base que alimenta todo o cálculo do benefício. | CADPROG #4 · `CADPROG.NSN#L86-L88` | blocks-stage-2 | Critical | Buscar `0.347215`/`FATOR-K` em `CALCBENF.NSN` e `CALCCORR.NSN`. Cruzar com `legacy-docs/REGRAS-NEGOCIO-2012.md` §6 ("Fator K — precisa análise"). Resolver antes de qualquer EARS de cálculo. |
| MYS-010 | **Fórmula de cálculo MULTIPLICATIVA diverge da RN-013 ADITIVA** — o benefício é `VLR-BASE × FATOR-REG × FATOR-FAM × FATOR-RENDA × FATOR-IDADE` (e o fator familiar é escalonado 0,05/0,03/0,02), mas a RN-013 documenta soma aditiva (`VALOR-BASE + ACRESCIMO-DEPEND × QT-DEPEND`). O cálculo de produção não corresponde à regra documentada. | CALCBENF #6 e #9 · `CALCBENF.NSN#L186-L198`; `CALCBENF.NSN#L222-L223` | blocks-stage-2 | Critical | Decisão de domínio obrigatória: qual fórmula é a correta? Validar com facilitador/folha de pagamento real antes de escrever EARS de cálculo. Impacto financeiro direto em todos os benefícios. |
| MYS-012 | **Fórmula do 13º: comentário ≠ código** — o comentário diz `VLR_BASE × FATOR_REG × (MESES_ATIVOS/12)`, mas o código usa `VLR_BASE × FATOR_REG × FATOR_IDADE`, sem proporcionalidade por meses ativos. | CALCBENF #12 · `CALCBENF.NSN#L240-L246` (replicado em `BATCHPGT.NSN#L283-L294`) | blocks-stage-2 | Critical | Confirmar com facilitador a fórmula correta do 13º (proporcional a meses ativos vs fator idade). Verificar se algum outro processo usa MESES_ATIVOS. Afeta valor pago em dezembro. |
| MYS-013 | **Fonte da verdade do desconto** — existem TRÊS cálculos de desconto divergentes: 3% fixo p/ bruto>500 em CALCBENF, 3% fixo em BATCHPGT (pagamento real), e progressivo 3/5/7/9% por faixa em CALCDSCT. O pagamento mensal usa o simplificado; CALCDSCT (completo) não é chamado pelo batch. | CALCBENF #14 (`CALCBENF.NSN#L305-L314`) · CALCDSCT #3 (`CALCDSCT.NSN#L100`,`#L188-L200`) · BATCHPGT #8 (`BATCHPGT.NSN#L297-L303`) | blocks-stage-2 | Critical | Decidir qual cálculo é a regra oficial. Confirmar se beneficiários são descontados a menor no batch. Unificar antes de qualquer EARS de desconto. Cruzar com alteração 2015 ("NOVAS ALIQUOTAS"). |
| MYS-016 | **Pensão alimentícia (`P`) submetida ao teto de 30%** — apenas o desconto judicial (`J`) é exceção ao teto de 30% do bruto; pensão (`P`), que juridicamente costuma ser impenhorável, é limitada pelo teto. Possível defeito jurídico ou regra deliberada não documentada. | CALCDSCT #7 · `CALCDSCT.NSN#L137-L145`,`#L166-L171` | blocks-stage-2 | Critical | Questão jurídica/de domínio: pensão deve ou não ser exceção ao teto? Validar com facilitador/jurídico antes de escrever a EARS do teto de desconto (BR-013). |
| MYS-017 | **Bypass de elegibilidade da região 99** — `IF COD-REGIAO = 99 → ELEGIVEL + ESCAPE ROUTINE`, curto-circuitando TODA validação (status, idade, renda, tipo, documentos). Rotulado "INTERNACIONAL/DIPLOMATICO", incluído em 05/04/2013. Resolve o "bypass do Roberto" (RN-005), mas é vetor de fraude. | VALELEG #4 · `VALELEG.NSN#L107-L111` | blocks-stage-2 | Critical | Mecanismo COMPREENDIDO; falta **decisão de segurança/produto**: replicar (com controle de acesso) ou eliminar o bypass no sistema modernizado. Cruzar com fator neutro de região 99 em `CALCBENF` (MYS-009). |
| MYS-019 | **Backdoor de CPF de teste (`000`)** — CPFs com todos os 11 dígitos iguais são inválidos, EXCETO se começarem com `000`, aceitos como "TESTE GOVERNO". Permite cadastros fictícios em produção. | VALBENEF #2 · `VALBENEF.NSN#L210-L223` | blocks-stage-2 | Critical | Decisão de segurança: remover backdoor de teste do fluxo de produção. Relacionar com MYS-021 (lista de prefixos de VALDOCS). |
| MYS-021 | **Backdoor de prefixos especiais (VALDOCS)** — se os 3 primeiros dígitos do CPF ∈ {000,001,002,010,011,099,100,999}, a subrotina CHECK-DOC-ESPECIAL força `RESULTADO='V'`, `CPF-OK=TRUE`, `QTD-ERROS=0`, anulando TODA a validação documental. Ajustado em 2011. | VALDOCS #3 · `VALDOCS.NSN#L186-L205` (tabela `L41-L48`) | blocks-stage-2 | Critical | Vetor de fraude grave. Decisão de segurança obrigatória: eliminar o bypass. Investigar quem/por que usa prefixos 099/100/999. Relacionar com MYS-019. |
| MYS-029 | **Exclusões ocultadas da trilha de auditoria** — o relatório de auditoria filtra e oculta eventos `ACAO='EX'` (exclusão), justamente os mais sensíveis. Introduzido na alteração 2014 "LIMPEZA RELATORIO". | RELAUDIT #3 · `RELAUDIT.NSN#L120-L123` | blocks-stage-2 | Critical | Bandeira vermelha de compliance. Decisão obrigatória: a trilha modernizada DEVE exibir exclusões. Verificar se eventos `EX` ao menos são gravados (vs nunca registrados). |
| MYS-002 | **Status `S` para idade > 75** — beneficiário com mais de 75 anos recebe status `S` (suspenso), sobrescrevendo `A`. Magic number 75; introduzido em 10/01/2011 ("AJUSTE STATUS IDOSO"). Significado de `S`=SUSPENSO agora confirmado, mas a regra dos 75 anos não tem fonte documental. | CADBENEF #11 · `CADBENEF.NSN#L166-L169` | needs-investigation | High | `S`=SUSPENSO resolvido (ver MYS-004). Investigar onde `STATUS='S'` é **consumido** (`CALCBENF`, `BATCHPGT`, `VALELEG#L116-L134`) e por que 75 anos suspende automaticamente. Confirmar com facilitador se é regra de negócio. |
| MYS-003 | **Defeito latente de status em branco** — em alteração de beneficiário não-idoso, `#STATUS` (vazio) é gravado no UPDATE, podendo zerar o status do registro. | CADBENEF #14 · `CADBENEF.NSN#L161-L169` | needs-investigation | High | Verificar comportamento do Natural ao gravar variável `(A1)` não inicializada (branco vs preservado). Confrontar com fluxo de alteração `CADBENEF.NSN#L177-L219`. Pode escalar para blocker se confirmar corrupção de status. |
| MYS-005 | **Validações prometidas e ausentes em dependentes** — Manual 3.2.2 diz "validação de CPF e data de nascimento"; o código não valida DT-NASC-DEP nem aplica dígito verificador ao CPF-DEP. | CADDEPEND #9 · `CADDEPEND.NSN#L67-L124` | needs-investigation | High | A validação de CPF existe em `VALBENEF.NSN`/`VALDOCS.NSN`, mas **nenhuma é chamada por CADDEPEND** (sem CALLNAT). Confirmar que o cadastro de dependente realmente não valida — lacuna real de regra a documentar na EARS. |
| MYS-007 | **Sem validação de quantidade de dependentes e NIS no CADBENEF** — NUM-DEPENDENTES e NIS gravados sem checagem; RN-004 exige limite e RN-001 exige validação de NIS via VALNISN. | CADBENEF #15 · `CADBENEF.NSN#L77-L219` | needs-investigation | High | Parcial: `CADDEPEND.NSN#L62-L65` aplica limite (porém **5**, não 3 — discrepância RN-004); `VALELEG#L223-L242` exige NIS quando COD-ELEG pos1=`R`. Para NIS no cadastro, procurar `CALLNAT 'VALNISN'` (não materializado nos 15 `.NSN`). |
| MYS-014 | **Tabela IPCA incompleta — correção silenciosamente nula** — CALC-INDICE-ACUM só aplica índice para anos 2010–2012; para qualquer outro ano não há ELSE e o índice fica 1,0, sem aviso. Pagamentos fora dessa janela não são corrigidos. | CALCCORR #5 · `CALCCORR.NSN#L180-L186` (tabela `L55-L97`) | needs-investigation | High | Confirmar se a tabela `#IPCA-ANO` deveria conter outros anos (comentário diz "última carga 2014" mas só há 2010-2012). Definir comportamento esperado p/ anos ausentes (erro vs índice 1,0). Cruzar com RN-019. |
| MYS-022 | **Dedup só de CPF adjacente** — o batch ignora duplicata apenas quando o CPF é igual ao do registro IMEDIATAMENTE anterior (`#CPF-ANT`). Duplicatas não adjacentes escapam. Por que há CPFs duplicados num arquivo de chave única? | BATCHPGT #2 · `BATCHPGT.NSN#L205-L210` | needs-investigation | High | Investigar a origem de CPFs duplicados em BENEFICIARIO (indício de dado sujo). Verificar se a leitura `BY CPF` garante adjacência. Definir regra de unicidade real para a migração. |
| MYS-023 | **Batch reimplementa CALCBENF inline (sem CALLNAT)** — o cabeçalho diz "CHAMA CALCBENF E CALCDSCT", mas BATCHPGT copia toda a fórmula e as tabelas de fatores inline. Dois cálculos independentes do mesmo benefício = divergência quando um for alterado e o outro não. | BATCHPGT #6 · `BATCHPGT.NSN#L249-L310` | needs-investigation | High | Comparar linha a linha o cálculo de BATCHPGT com `CALCBENF.NSN` para detectar divergências já existentes (tabelas, faixas, magic numbers). Na migração, unificar numa única regra de cálculo. Relaciona-se com MYS-010/MYS-013. |
| MYS-025 | **Bucketing de macro-região distorce o relatório** — o relatório consolidado mapeia código→macro-região hardcoded (1-5→Norte … 16-20→Sul) e o ELSE joga TUDO fora de 1-20 (incl. região 99 e códigos 21-25) em "Centro-Oeste". Não confere com a tabela de 27 UFs de VALBENEF. | BATCHREL #1 · `BATCHREL.NSN#L138-L155` | needs-investigation | High | Levantar o mapeamento real código-de-região → UF/macro-região (cruzar com `#TAB-REG` de CALCBENF e `#UF-TAB` de VALBENEF). Corrigir o destino de 21-25 e 99 antes de confiar nos totais regionais. |
| MYS-026 | **Relatório ARREDONDA, pagamento TRUNCA** — o relatório soma 0,005 antes de truncar (arredonda), com comentário explícito "ARREDONDAMENTO DIFERE DO CALCBENF (ROUND VS TRUNCATE)". Os totais gerenciais não batem com a soma dos pagamentos (que truncam — RN-014). | BATCHREL #2 · `BATCHREL.NSN#L158-L163` | needs-investigation | High | Quantificar a divergência de conciliação contábil. Definir a regra única de arredondamento/truncamento para o sistema modernizado (pagamento e relatório devem reconciliar). |
| MYS-028 | **Tipo de pagamento `T` (terceiro) órfão** — RELPGT traduz TIPO-PGTO `T`→TERCEIRO, mas nem CALCBENF nem BATCHPGT geram `T` (só `N` e `D`). De onde vem um pagamento tipo `T`? | RELPGT #5 · `RELPGT.NSN#L131-L140` | needs-investigation | High | Buscar `MOVE 'T' TO ...TIPO-PGTO`/`TIPO-PGTO = 'T'` nos 15 `.NSN` para achar o produtor. Se nenhum, é domínio morto ou processo externo/manual desconhecido. |
| MYS-030 | **Validadores `VALBENEF` e `VALDOCS` são órfãos** — declaram view de BENEFICIARIO mas não acessam dados (sem READ/FIND/STORE/UPDATE) e **nenhum programa os chama** (zero CALLNAT em todo o legado). Contêm a validação de CPF mod-11, data, nome, RG e os backdoors de prefixo (MYS-019/MYS-021), mas ninguém materializado os invoca. | dependency-map.md (Órfãos) · `dependency-map.md#L189-L196` | needs-investigation | High | Buscar `VALBENEF`/`VALDOCS` como alvo de CALLNAT em todo o legado (já feito: **zero**). Decidir se são código morto/não integrado ou se eram chamados por programas online (MAP) ausentes da pasta. Relaciona-se com MYS-005 (CADDEPEND não valida CPF/data do dependente). |
| MYS-031 | **Subprogramas esperados ausentes: `VALCPF`, `VALNISN`, copycode `FMTVLR`** — citados na documentação/cabeçalhos, mas não existem entre os 15 `.NSN` e nunca são chamados (zero CALLNAT/INCLUDE). A validação de NIS (RN-001 via VALNISN) não ocorre em lugar nenhum materializado. | dependency-map.md (Referências Quebradas) · `dependency-map.md#L172-L177`; [inventory.md](inventory.md) | needs-investigation | High | Confirmar com o facilitador/DBA se esses subprogramas existem no acervo original (perdidos na extração) ou se foram inlined/renomeados. Buscar `VALCPF`/`VALNISN`/`FMTVLR` em todo o legado (feito: ausentes). Impacta a rastreabilidade RN-001 (NIS). |
| MYS-008 | **Domínio de TIPO de programa (A/P/T) só comentado, nunca validado** — `A=ASSISTENCIAL P=PREVIDENC T=TRABALHO` existe só como comentário no DEFINE DATA; nenhum `IF` valida `#TIPO` antes de gravar. | CADPROG #7 · `CADPROG.NSN#L15` | needs-facilitator | Medium | Confirmar com facilitador se A/P/T é domínio fechado que o sistema modernizado deve impor. `VALELEG#L168-L201` trata A/P/T (e "desconhecido→inelegível"), sugerindo domínio fechado. Decisão de produto. |
| MYS-009 | **Tabela de fatores regionais (magic numbers)** — `#TAB-REG(27)` traz 27 fatores (1,35→1,03) hardcoded sem doc; mapeamento código→UF inconsistente (índice 15='REF'=1,0; 26/27='RESERVA'); região 99 cai no ELSE e recebe fator neutro 1,0. | CALCBENF #5 · `CALCBENF.NSN#L179-L183` (tabela `L88-L117`) | needs-facilitator | Medium | Obter a tabela oficial de fatores regionais com o facilitador/área de benefícios. Efeito da região 99 (fator neutro) relaciona-se ao bypass de MYS-017. Sem doc, não há como validar os valores. |
| MYS-011 | **Fator idade (magic numbers)** — ≥65→1,15; ≥60→1,10; <18→1,05; demais→1,00, sem documentação; idade calculada só por diferença de anos (imprecisa). | CALCBENF #8 · `CALCBENF.NSN#L204-L216` | needs-facilitator | Medium | Confirmar com facilitador as faixas etárias e fatores oficiais. Decidir cálculo de idade correto (com mês/dia) para o sistema modernizado. |
| MYS-018 | **Código de elegibilidade posicional (COD-ELEGIBILIDADE A5)** — código críptico onde cada caractere liga uma regra (pos1=`R`→exige NIS; pos2=`D`→exige dependentes). Só 2 das 5 posições são interpretadas; significado das posições 3-5 é desconhecido. RN-015 está PENDENTE. | VALELEG #9 · `VALELEG.NSN#L206-L207`,`#L223-L242` | needs-facilitator | Medium | Pedir ao facilitador a especificação do COD-ELEGIBILIDADE (RN-015 pendente). Histograma dos valores reais de COD-ELEG no Adabas pode revelar as posições 3-5 em uso. |
| MYS-027 | **Mascaramento de CPF inconsistente / com vazamento (LGPD)** — CONSBENF tem defeito CONHECIDO ("NAO CORRIGIR SEM APROVACAO DA AUDITORIA"): para CPF < 11 dígitos mostra os 3 PRIMEIROS dígitos. RELPGT usa máscara DIFERENTE (`***.NNN.NNN-NN`), expondo 8 dígitos. Mascaramento divergente entre programas. | CONSBENF #3 (`CONSBENF.NSN#L175-L195`) · RELPGT #4 (`RELPGT.NSN#L124-L128`) | needs-facilitator | Medium | Decisão de privacidade/LGPD: definir um padrão único de mascaramento de CPF para todo o sistema modernizado. O sistema atual viola minimização de dados; alinhar com DPO/auditoria. |
| MYS-004 | ✅ **RESOLVIDO** — **Status `C`/`D` do titular** — inclusão de dependente bloqueada quando titular está em `C`/`D`. Significado antes desconhecido. | CADDEPEND #2 · `CADDEPEND.NSN#L55-L58` | parked | Low | Resolvido por evidência: `VALELEG#L116-L134` define o domínio fechado **A**=ativo, **S**=suspenso, **C**=cancelado, **D**=desligado, **I**=inativo (confirmado por VALBENEF#7 e CONSBENF#4). Documentar o domínio e seguir. |
| MYS-006 | ✅ **RESOLVIDO** — **Elegibilidade/renda/idade não validadas no CADPROG** — campos gravados sem `IF` no cadastro de programa. | CADPROG #8 · `CADPROG.NSN#L60-L101` | parked | Low | Resolvido por evidência: a validação de elegibilidade não fica no cadastro — vive em `VALELEG.NSN` (renda máx `#L157-L163`, idade `#L139-L152`, tipo A/P/T `#L168-L201`). CADPROG apenas persiste os parâmetros. |
| MYS-015 | **Bloco "Plano Verão" morto** — bloco comentado ("NAO REMOVER - HISTORICO", João Batista 2003) corrige competências 01/1989–01/1991 com fatores 2,75/1,4289 (transição Cruzado→Cruzeiro). | CALCCORR #8 · `CALCCORR.NSN#L98-L110` | parked | Low | Código morto. Documentar como histórico; só reativar se a migração precisar reprocessar correções de moedas antigas. Sem ação imediata. |
| MYS-020 | **29/02 aceito em ano não bissexto** — `#DIAS-MES(2)=29` fixo ("CONSIDERA BISSEXTO"), mas não há cálculo de ano bissexto; 29/02 é aceito em qualquer ano. Defeito de validação de data. | VALBENEF #4 · `VALBENEF.NSN#L271-L274` | parked | Low | Defeito conhecido; o sistema modernizado deve validar ano bissexto corretamente. Sem investigação necessária — apenas registrar como requisito de correção. |
| MYS-024 | **Bloco "Banco Real" morto** — integração comentada com retorno do Banco Real (cód. 356, layout distinto do BB), "DESCONTINUADA" (adquirido pelo Santander em 2007). | BATCHCON #6 · `BATCHCON.NSN#L226-L242` | parked | Low | Código morto. Indica que houve conciliação multi-banco com layouts diferentes. Documentar; relevante só se a migração precisar ler arquivos de retorno antigos. |

## Detalhamento dos Mistérios

### MYS-001: [Título do Mistério]

- **Arquivo**: `01-arqueologia/legado-sifap/natural-programs/ARQUIVO.NSN#L<inicio>-L<fim>`
- **Trecho de código**:

```natural
* Cole aqui o trecho relevante
```

- **O que esperávamos**: [comportamento esperado]
- **O que o código faz**: [comportamento real]
- **Hipótese do time**: [melhor palpite]
- **Risco se ignorarmos**: [o que pode dar errado na migração]

---

> Copie o bloco acima para cada mistério encontrado.

## Easter Eggs

> Dica: existem **3 easter eggs** escondidos no código legado. Registre aqui os que encontrar:

1. [ ] Easter Egg 1: \_\_\_
2. [ ] Easter Egg 2: \_\_\_
3. [ ] Easter Egg 3: \_\_\_

## Resumo

- Total de mistérios catalogados: **31** (consolidados de **35** marcadores `<!-- mystery: -->`; 4 marcadores fundidos em 3 entradas: MYS-010, MYS-013, MYS-027; +2 novos de dependência: MYS-030, MYS-031)
- blocks-stage-2 (Critical): **9** — MYS-001, MYS-010, MYS-012, MYS-013, MYS-016, MYS-017, MYS-019, MYS-021, MYS-029
- needs-investigation (High): **12** — MYS-002, MYS-003, MYS-005, MYS-007, MYS-014, MYS-022, MYS-023, MYS-025, MYS-026, MYS-028, MYS-030, MYS-031
- needs-facilitator (Medium): **5** — MYS-008, MYS-009, MYS-011, MYS-018, MYS-027
- parked (Low): **5** — MYS-004 ✅, MYS-006 ✅, MYS-015, MYS-020, MYS-024
- Mistérios resolvidos por evidência (✅): **2** — MYS-004 e MYS-006 (resolvidos por `VALELEG.NSN`)
- Mistérios novos desta re-execução (dependências): **2** — MYS-030 (órfãos VALBENEF/VALDOCS) e MYS-031 (subprogramas VALCPF/VALNISN/FMTVLR ausentes)
- Easter eggs encontrados: **0 / 3** (não pesquisados nesta passada — varredura focada nos marcadores `<!-- mystery: -->`)

> **Cobertura:** os 15 programas `.NSN` foram lidos e tiveram regras extraídas em
> [`business-rules-catalog.md`](business-rules-catalog.md). Todos os 33 marcadores `<!-- mystery: -->`
> daquele catálogo estão representados aqui. [`dependency-map.md`](dependency-map.md) **foi preenchido**
> com arestas reais e re-escaneado: confirmou **zero CALLNAT/INCLUDE** em todo o legado (integração só
> por DDMs compartilhados) e adicionou 2 mistérios de nível de dependência (MYS-030, MYS-031).
>
> **Mistérios resolvidos nesta rodada (não eram conhecidos antes):** domínio de status de
> beneficiário `A`/`S`/`C`/`I`/`D` (via VALELEG/VALBENEF/CONSBENF), status de pagamento
> `G`/`P`/`C`/`D`/`E` (via BATCHCON/BATCHREL/RELPGT) e a natureza do **bypass da região 99**
> (RN-005, resolvido em VALELEG — agora reclassificado como risco de segurança em MYS-017).

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="mysteries-checklist.md"><strong>mysteries-checklist.md</strong></a><br/>
<sub>Lista do que procurar.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="discovery-report.md"><strong>discovery-report.md</strong></a><br/>
<sub>Síntese final.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="README.md">Voltar ao Kit PT-BR</a></sub>

