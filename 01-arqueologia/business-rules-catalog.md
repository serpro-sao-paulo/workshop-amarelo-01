<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Catálogo de Regras de Negócio — SIFAP Legado

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **business-rules-catalog**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Registre aqui todas as regras de negócio extraídas do código Natural/Adabas.
> Cada regra precisa ter rastreabilidade até o código-fonte.
>
> **REGRA DURA:** linhas com `Programa Fonte` vazio são **inválidas** e não contam para o gate do Estágio 2. Use o formato `01-arqueologia/legado-sifap/natural-programs/ARQUIVO.NSN#L<inicio>-L<fim>` sempre que possível. Mínimo aceito: nome do arquivo .NSN.

## Como pensar em "regra de negócio"

O que conta:

- Um `IF` que decide algo no domínio (ex.: _"se a UF é do Nordeste e o programa é Seca, valor base × 1.2"_)
- Uma constante numérica sem explicação (ex.: `0.075` num cálculo de imposto)
- Uma transição de status com regra (ex.: _"só de A para S, nunca de I para A"_)
- Um tratamento especial para um caso (ex.: _"se o CPF começa com 999, é teste"_)

O que NÃO conta: paginação de relatório, formatação de saída, manipulação de cursor Adabas, abertura de arquivo. Ignore esses detalhes de implementação.

## Níveis de Risco

| Nível       | Descrição                                                     |
| ----------- | ------------------------------------------------------------- |
| **CRÍTICO** | Regra financeira ou de segurança — erro causa prejuízo direto |
| **ALTO**    | Regra de negócio central — afeta fluxo principal              |
| **MÉDIO**   | Regra de validação ou formatação — afeta qualidade dos dados  |
| **BAIXO**   | Regra de apresentação ou conveniência — impacto limitado      |

## Regras Encontradas

| ID     | Regra de Negócio | Programa Fonte | Campos DDM | Nível de Risco | Notas |
| ------ | ---------------- | -------------- | ---------- | -------------- | ----- |
| BR-001 |                  |                |            |                |       |
| BR-002 |                  |                |            |                |       |
| BR-003 |                  |                |            |                |       |
| BR-004 |                  |                |            |                |       |
| BR-005 |                  |                |            |                |       |
| BR-006 |                  |                |            |                |       |
| BR-007 |                  |                |            |                |       |
| BR-008 |                  |                |            |                |       |
| BR-009 |                  |                |            |                |       |
| BR-010 |                  |                |            |                |       |
| BR-011 |                  |                |            |                |       |
| BR-012 |                  |                |            |                |       |
| BR-013 |                  |                |            |                |       |
| BR-014 |                  |                |            |                |       |
| BR-015 |                  |                |            |                |       |

> Adicione mais linhas conforme necessário. Lembre-se: existem **10 regras escondidas** no código!

## Exemplo de linha bem preenchida

| ID     | Regra de Negócio                                                                        | Programa Fonte                                   | Campos DDM                                                               | Nível de Risco | Notas                                      |
| ------ | --------------------------------------------------------------------------------------- | ------------------------------------------------ | ------------------------------------------------------------------------ | -------------- | ------------------------------------------ |
| BR-013 | Desconto total não pode exceder 30% do valor bruto, exceto descontos judiciais (tipo J) | `01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L142-L148` | `PAGAMENTO.VLR-BRUTO`, `PAGAMENTO.VLR-TOTAL-DSCT`, `PAGAMENTO.TIPO-DSCT` | CRÍTICO        | Regra financeira. Tipo 'J' = exceção legal |

## Regras por Categoria

### Cálculos Financeiros

<!-- Liste aqui as regras relacionadas a cálculos de valores, benefícios, etc. -->

### Validações de Status

<!-- Liste aqui as regras de transição de status (A, S, C, I, D) -->

### Regras de Autorização

<!-- Liste aqui as regras de quem pode fazer o quê -->

### Regras de Negócio Temporais

<!-- Liste aqui regras com prazos, datas-limite, períodos -->

## Regras de CADBENEF.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (272 linhas):
> `01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN`.
> Cross-reference com `legacy-docs/REGRAS-NEGOCIO-2012.md` (RN-xxx) e
> `legacy-docs/MANUAL-TECNICO-SIFAP-2008.md` (seção 3.2.1).
> **Vocabulário (DEFINE DATA, L11-69):** view `BENEFICIARIO-V` (CPF N11, NOME A60,
> DT-NASCIMENTO N8, SEXO A1, STATUS A1, COD-PROGRAMA N4, RENDA-FAMILIAR N9.2,
> NUM-DEPENDENTES N2, COD-REGIAO N2, NIS N11, DT-CADASTRO/DT-ATUALIZACAO N8) + campos de
> trabalho `#...` e variáveis do algoritmo de CPF (`#DIG`, `#SOMA`, `#RESTO`, `#DV1/2`, `#PESO`).

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | Se a operação informada não for `I` (inclusão) nem `A` (alteração), então o sistema deverá rejeitar com "OPERACAO INVALIDA". | Unwanted | CADBENEF.NSN#L99-L103 | Inferida | O Manual 3.2.1 lista também exclusão (SF03, status `E`). **Esta versão do código só trata I e A — exclusão lógica NÃO existe aqui.** Possível defasagem entre doc e fonte. |
| 2  | Se o CPF for igual a zero, então o sistema deverá rejeitar com "CPF OBRIGATORIO". | Unwanted | CADBENEF.NSN#L105-L109 | Confirmada | RN-001 e Manual 3.2.1 (CPF obrigatório). |
| 3  | Se o CPF não passar na validação de dígito verificador, então o sistema deverá rejeitar com "CPF INVALIDO". | Unwanted | CADBENEF.NSN#L111-L117 | Confirmada | RN-001 (validação por dígito verificador). **Discrepância:** doc diz CALLNAT subprograma externo `VALCPF`; o código usa SUBROUTINE interna `VALIDA-CPF`. |
| 4  | O sistema deverá calcular o dígito verificador do CPF pelo módulo 11: peso decrescente 10→2 (1º DV) e 11→2 (2º DV); se o resto for menor que 2 o dígito é 0, caso contrário é 11 menos o resto. | Ubiquitous | CADBENEF.NSN#L224-L269 | Confirmada | RN-001. Algoritmo padrão mod-11 de CPF. |
| 5  | Se o nome estiver em branco, então o sistema deverá rejeitar com "NOME OBRIGATORIO". | Unwanted | CADBENEF.NSN#L119-L123 | Inferida | Sem RN explícito; coerente com Manual 3.2.1. |
| 6  | Se a data de nascimento for zero, então o sistema deverá rejeitar com "DATA NASCIMENTO OBRIGATORIA". | Unwanted | CADBENEF.NSN#L125-L129 | Confirmada | RN-006 (DT-NASC obrigatória). **Lacuna:** RN-006 também exige idade mínima de 16 anos — **não implementada no código**. |
| 7  | Se o sexo informado não for `M` nem `F`, então o sistema deverá rejeitar com "SEXO INVALIDO". | Unwanted | CADBENEF.NSN#L131-L135 | Inferida | Sem suporte documental. |
| 8  | Quando a operação for inclusão e já existir beneficiário com o mesmo CPF, o sistema deverá rejeitar com "BENEFICIARIO JA CADASTRADO". | Unwanted | CADBENEF.NSN#L137-L147 | Confirmada (parcial) | RN-002. **Discrepância importante:** RN-002 só bloqueia CPF em situação ATIVA (`STATUS='A'`) e permite reinclusão de excluído (`E`); o código bloqueia qualquer CPF existente, **sem checar o STATUS**. |
| 9  | Quando a operação for alteração e não existir beneficiário com o CPF informado, o sistema deverá rejeitar com "BENEFICIARIO NAO ENCONTRADO PARA ALTERACAO". | Unwanted | CADBENEF.NSN#L149-L153 | Inferida | Sem RN explícito. |
| 10 | Quando um beneficiário for incluído, o sistema deverá atribuir status inicial `A` (ativo). | Event-driven | CADBENEF.NSN#L161-L164 | Confirmada | Coerente com RN-002/RN-011 (status ativo = `A`). |
| 11 | Enquanto a idade do beneficiário for maior que 75 anos, o sistema deverá atribuir status `S`, sobrescrevendo o status `A`. | State-driven | CADBENEF.NSN#L166-L169 | Mistério | <!-- mystery: significado do status 'S' (idoso?) não está em nenhuma doc de negócio; magic number 75; introduzido em 10/01/2011 "AJUSTE STATUS IDOSO" (cabeçalho L7). Glossário cita status A/S/C/I/D mas não define 'S'. --> |
| 12 | O sistema deverá calcular a idade apenas pela diferença de anos (ano atual − ano de nascimento), ignorando mês e dia. | Ubiquitous | CADBENEF.NSN#L155-L159 | Inferida | Imprecisão: não considera se o aniversário já ocorreu no ano. Impacta diretamente a regra dos 75 anos (#11). |
| 13 | Quando a operação for alteração, o sistema deverá atualizar somente dados cadastrais, NÃO alterando CPF, COD-PROGRAMA, COD-REGIAO, NIS nem DT-CADASTRO. | State-driven | CADBENEF.NSN#L177-L219 | Inferida | **Discrepância:** RN-009 prevê alteração de CPF com autorização nível 2 (SUPERVISOR); o código simplesmente **não permite** alterar CPF (campo não é regravado). Não há campo `BN-NR-CPF-ANT` na view. |
| 14 | Em alteração de beneficiário com idade ≤ 75, o status existente é sobrescrito por valor em branco, pois `#STATUS` só é preenchido na inclusão ou quando idade > 75. | Unwanted | CADBENEF.NSN#L161-L169 | Mistério | <!-- mystery: provável defeito latente — UPDATE (L209 aprox.) grava #STATUS que está vazio para operação 'A' de não-idoso, zerando o status do registro. Confirmar comportamento real do Natural com variável não inicializada. --> |
| 15 | Não há validação de quantidade de dependentes nem de NIS na inclusão/alteração. | (lacuna) | CADBENEF.NSN#L77-L219 | Mistério | <!-- mystery: RN-004 exige limite de dependentes (3, ou 5?) e RN-001 exige validação de NIS via VALNISN; nenhum dos dois é verificado neste programa. NUM-DEPENDENTES e NIS são apenas gravados sem checagem. --> |

> **Resumo CADBENEF.NSN:** 15 candidatas — 6 confirmadas (1 parcial), 6 inferidas, 3 mistérios.
> Achados de maior risco: status `S` para >75 (não documentado), possível zeramento de STATUS em
> alteração, bloqueio de duplicidade sem checar STATUS (impede reinclusão de excluído), e ausência
> de validações exigidas pelas RNs (idade mínima 16, limite de dependentes, NIS).

## Regras de CADDEPEND.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (134 linhas):
> `01-arqueologia/legado-sifap/natural-programs/CADDEPEND.NSN`.
> Cross-reference com `legacy-docs/MANUAL-TECNICO-SIFAP-2008.md` (seção 3.2.2) e
> `legacy-docs/REGRAS-NEGOCIO-2012.md` (RN-004).
> **Vocabulário (DEFINE DATA, L10-37):** view `BENEFICIARIO-V` (CPF N11, NOME A60, STATUS A1,
> NUM-DEPENDENTES N2) + grupo periódico `DEPENDENTES (PE)` com NOME-DEP (A60), DT-NASC-DEP (N8),
> PARENTESCO (A2 — comentário `FI=FILHO CO=CONJUGE IR=IRMAO`), CPF-DEP (N11), DOC-DEP (A15),
> SEXO-DEP (A1); campos de trabalho `#...` e contadores `#NUM-DEP`, `#IDX`, `#CONT`.
> O cabeçalho (L6) registra alteração 14/03/2008 por ROBERTO MENDES — "AJUSTE PE GROUP".

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | Se o CPF do titular informado não existir no arquivo de beneficiários, então o sistema deverá rejeitar com "BENEFICIARIO NAO ENCONTRADO". | Unwanted | CADDEPEND.NSN#L50-L53 | Confirmada | Manual 3.2.2 (vinculação ao titular pela chave CPF). |
| 2  | Se o beneficiário titular estiver com status `C` ou `D`, então o sistema deverá rejeitar com "BENEFICIARIO CANCELADO/DESLIGADO - NAO PERMITE INCLUSAO". | Unwanted | CADDEPEND.NSN#L55-L58 | Mistério | <!-- mystery: status 'C' e 'D' não definidos em nenhuma doc de negócio (glossário cita A/S/C/I/D sem descrever C/D). 'CANCELADO/DESLIGADO' vem só do literal de tela. Confirmar significado e como o titular chega a esse status. --> |
| 3  | Enquanto a quantidade de dependentes do titular for maior que 5, o sistema deverá impedir novas inclusões com "LIMITE DE DEPENDENTES ATINGIDO". | State-driven | CADDEPEND.NSN#L62-L65 | Confirmada (parcial) | **Discrepância:** RN-004 e Manual 3.2.2 declaram limite de **3**; o código usa **5**. Confirma a nota da RN-004 ("indícios de alteração para 5"). Magic number `5` hardcoded. |
| 4  | Se o nome do dependente estiver em branco, então o sistema deverá rejeitar com "NOME DO DEPENDENTE OBRIGATORIO". | Unwanted | CADDEPEND.NSN#L78-L81 | Inferida | Sem RN explícito; coerente com cadastro. |
| 5  | Se o parentesco informado não for `FI`, `CO`, `IR` ou `OU`, então o sistema deverá rejeitar com "PARENTESCO INVALIDO". | Unwanted | CADDEPEND.NSN#L83-L87 | Confirmada (parcial) | Manual 3.2.2 cita tipos "cônjuge, filho, outro" (3); o código aceita **4**, incluindo `IR` (irmão) — não documentado. Comentário L19 lista apenas FI/CO/IR. |
| 6  | Se o CPF do dependente já existir no grupo de dependentes do titular (e for diferente de zero), então o sistema deverá rejeitar com "DEPENDENTE JA CADASTRADO (CPF DUPLICADO)". | Unwanted | CADDEPEND.NSN#L94-L103 | Inferida | Checa duplicidade apenas quando `CPF-DEP NE 0` — **vários dependentes sem CPF (=0) são permitidos** sem bloqueio de duplicidade. |
| 7  | Quando um dependente válido for incluído, o sistema deverá gravá-lo na próxima posição do grupo periódico, incrementar NUM-DEPENDENTES e confirmar com "DEPENDENTE INCLUIDO - TOTAL". | Event-driven | CADDEPEND.NSN#L110-L124 | Inferida | Estrutura de gravação no PE. O contador `#NUM-DEP` é incrementado e regravado em `NUM-DEPENDENTES`. |
| 8  | O sistema deverá repetir o cadastro de dependentes enquanto o operador responder `S` à pergunta "INCLUIR OUTRO DEPENDENTE?"; qualquer outra resposta encerra o loop. | Event-driven | CADDEPEND.NSN#L127-L130 | Inferida | Controle de fluxo da tela; não é regra de domínio estrita. |
| 9  | Não há validação da data de nascimento do dependente nem do dígito verificador do CPF do dependente. | (lacuna) | CADDEPEND.NSN#L67-L124 | Mistério | <!-- mystery: Manual 3.2.2 afirma "inclusão de dependente com validação de CPF e data de nascimento"; o código NÃO valida DT-NASC-DEP nem aplica mod-11 ao CPF-DEP. Também não há regra de idade/parentesco (ex.: cônjuge não pode ser menor). Lacuna entre doc e fonte. --> |

> **Resumo CADDEPEND.NSN:** 9 candidatas — 2 confirmadas, 2 confirmadas (parciais), 3 inferidas, 2 mistérios.
> Achados de maior risco: limite de dependentes `5` no código vs `3` na doc (RN-004); status `C`/`D`
> do titular sem definição; parentesco `IR` não documentado; ausência de validação de data de
> nascimento e de dígito verificador do CPF do dependente prometidas pelo Manual 3.2.2.

## Regras de CADPROG.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (123 linhas):
> `01-arqueologia/legado-sifap/natural-programs/CADPROG.NSN`.
> Cross-reference com `legacy-docs/MANUAL-TECNICO-SIFAP-2008.md` (seção 3.2.3) e
> `legacy-docs/REGRAS-NEGOCIO-2012.md`.
> **Vocabulário (DEFINE DATA, L11-44):** view `PROGRAMA-V` de `PROGRAMA-SOCIAL` — COD-PROGRAMA (N4),
> NOME-PROGRAMA (A60), TIPO (A1 — comentário `A=ASSISTENCIAL P=PREVIDENC T=TRABALHO`), VLR-BASE
> (N9.2), COD-ELEGIBILIDADE (A5), DT-INICIO/DT-FIM (N8), STATUS-PROG (A1), RENDA-MAX (N9.2),
> IDADE-MIN/IDADE-MAX (N3), FATOR-REAJUSTE (N3.4); campos de trabalho `#...` incluindo o
> `#FATOR-K` (N5.6) e `#VLR-CALC` (N9.2). Cabeçalho registra alterações: 05/07/2003 ("INC FATOR
> CORRECAO") e 18/11/2012 ("NOVOS COD ELEG").

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | Se a operação informada não for `I` (inclusão) nem `C` (consulta), então o sistema deverá rejeitar com "OPERACAO INVALIDA". | Unwanted | CADPROG.NSN#L49-L52 | Inferida | **Discrepância:** Manual 3.2.3 descreve "inclusão e alteração" de programas; o código só oferece inclusão e consulta — **não há alteração**. |
| 2  | Quando a operação for consulta (`C`), o sistema deverá exibir os dados do programa e, se não encontrado, informar "PROGRAMA NAO ENCONTRADO". | Event-driven | CADPROG.NSN#L54-L57; L107-L122 | Inferida | Subrotina `CONSULTA-PROG`. O `IF *NUMBER(PROGRAMA-V) = 0` (L118) trata ausência. |
| 3  | Quando a operação for inclusão e já existir programa com o mesmo COD-PROGRAMA, o sistema deverá rejeitar com "PROGRAMA JA CADASTRADO". | Unwanted | CADPROG.NSN#L76-L84 | Inferida | Sem RN explícito; impede duplicidade de código de programa. |
| 4  | Ao incluir um programa, o sistema deverá calcular o valor-base ajustado pela fórmula `VLR-CALC = VLR-BASE × (1.00 + FATOR-REAJUSTE × 0.347215)` e gravar esse valor ajustado (não o informado) em VLR-BASE. | Event-driven | CADPROG.NSN#L86-L88 | Mistério | <!-- mystery: constante 0.347215 (FATOR-K) é número mágico sem qualquer documentação; é a "fator-K" citada na RN-013/2012 que "ninguém soube explicar" (Marcos Antônio). Risco financeiro: grava VLR-BASE adulterado na inclusão. --> |
| 5  | Ao incluir um programa, o sistema deverá atribuir status `A` (ativo) ao programa. | Event-driven | CADPROG.NSN#L97 | Confirmada | Coerente com PS-IN-ATIVO/STATUS ativo citado na RN-003 (programa ativo). |
| 6  | O programa pode ter data de fim igual a zero, significando vigência indeterminada. | Ubiquitous | CADPROG.NSN#L69 | Inferida | Regra implícita pelo literal de tela "(0=INDETERMINADO)"; valor 0 em DT-FIM. Não há validação de DT-INICIO ≤ DT-FIM. |
| 7  | O tipo de programa deve ser `A` (assistencial), `P` (previdenciário) ou `T` (trabalho). | Ubiquitous | CADPROG.NSN#L15 | Mistério | <!-- mystery: o domínio A/P/T existe apenas como comentário no DEFINE DATA (L15); NÃO há nenhum IF validando #TIPO no corpo. Valor é gravado sem checagem. Regra documentada na intenção, mas não implementada. --> |
| 8  | Não há validação de COD-ELEGIBILIDADE, faixa de renda, idade mínima/máxima nem do valor-base na inclusão. | (lacuna) | CADPROG.NSN#L60-L101 | Mistério | <!-- mystery: a alteração de 18/11/2012 "NOVOS COD ELEG" sugere regras de elegibilidade, mas COD-ELEG (A5), RENDA-MAX, IDADE-MIN/MAX são apenas capturados e gravados sem nenhum IF. RN-017/RN-018 (faixas) não aparecem aqui — possivelmente residem em CALCBENF/VALELEG. --> |

> **Resumo CADPROG.NSN:** 8 candidatas — 1 confirmada, 4 inferidas, 3 mistérios.
> Achados de maior risco: fator-K `0.347215` que adultera VLR-BASE na gravação (número mágico,
> impacto financeiro direto); domínio de TIPO (A/P/T) só comentado, nunca validado; ausência de
> operação de alteração prevista no Manual 3.2.3; e nenhuma validação de elegibilidade/renda/idade.

## Regras de CALCBENF.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (326 linhas):
> `01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN`.
> Cross-reference com `legacy-docs/REGRAS-NEGOCIO-2012.md` (RN-013/014/017/018/019/020) e
> `legacy-docs/MANUAL-TECNICO-SIFAP-2008.md` (seção 3.3.1).
> **Vocabulário (DEFINE DATA, L13-86):** views `BENEFICIARIO-V` (CPF, STATUS, COD-PROGRAMA,
> RENDA-FAMILIAR, NUM-DEPENDENTES, COD-REGIAO, DT-NASCIMENTO), `PAGAMENTO-V` (VLR-BRUTO/DESCONTO/
> LIQUIDO, COMPETENCIA, TIPO-PGTO `N=NORMAL D=DECIMO T=TERCEIRO`, VLR-ABONO), `PROGRAMA-V`
> (VLR-BASE, TIPO, FATOR-REAJUSTE). Tabelas: `#TAB-REG(27)` fatores regionais, `#FAIXA-RENDA(5)` +
> `#FATOR-FAIXA(5)` faixas de renda. Cabeçalho registra alterações 2001 (13º), 2004 (fator reg),
> 2009 (abono natalino), 2013 (novas faixas de renda).

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | Se o mês extraído da competência for menor que 1 ou maior que 12, então o sistema deverá rejeitar com "COMPETENCIA INVALIDA". | Unwanted | CALCBENF.NSN#L142-L145 | Inferida | Validação de borda da competência AAAAMM. |
| 2  | Se não existir beneficiário com o CPF informado, então o sistema deverá rejeitar com "BENEFICIARIO NAO ENCONTRADO". | Unwanted | CALCBENF.NSN#L155-L158 | Inferida | Pré-condição do cálculo. |
| 3  | Se o beneficiário não estiver com status `A` (ativo), então o sistema deverá rejeitar o cálculo com "BENEFICIARIO NAO ATIVO". | Unwanted | CALCBENF.NSN#L160-L163 | Confirmada | RN-021 / seção 5.1: apenas beneficiários ativos (`BN-CD-SIT='A'`) são processados no pagamento. |
| 4  | Se não existir programa social com o COD-PROGRAMA do beneficiário, então o sistema deverá rejeitar com "PROGRAMA NAO ENCONTRADO". | Unwanted | CALCBENF.NSN#L173-L176 | Inferida | Pré-condição: vínculo BN-CD-PROG → PS-CD-PROG. |
| 5  | O sistema deverá aplicar um fator regional ao valor do benefício conforme o código de região (1 a 25), usando a tabela interna; para região fora de 1–25, o fator é 1,0000. | Optional | CALCBENF.NSN#L179-L183; tabela L88-L117 | Mistério | <!-- mystery: tabela de 27 fatores regionais hardcoded (1.35→1.03) sem documentação; mapeamento código→UF inconsistente (índice 15='REF'=1.0, 26/27='RESERVA'). Região 99 (o "bypass do Roberto" citado na RN-005) cai no ELSE e recebe fator neutro 1,0 — efeito real desconhecido. --> |
| 6  | O sistema deverá calcular um fator familiar por faixa de dependentes: 0 dep → 1,00; 1–2 dep → 1,00 + (dep × 0,05); 3–4 dep → 1,10 + ((dep−2) × 0,03); 5+ dep → 1,16 + ((dep−4) × 0,02). | Ubiquitous | CALCBENF.NSN#L186-L198 | Mistério | <!-- mystery: acréscimo por dependente é MULTIPLICATIVO e escalonado com magic numbers (0.05/0.03/0.02), mas a RN-013 documenta acréscimo ADITIVO fixo (ACRESCIMO-DEPEND × QT-DEPEND). Fórmula do código diverge da documentada. --> |
| 7  | O sistema deverá determinar um fator de renda pela primeira faixa cujo teto seja ≥ renda familiar: ≤300→1,00; ≤600→0,85; ≤1000→0,70; ≤1500→0,55; resto→0,40. | Ubiquitous | CALCBENF.NSN#L201; subrotina L293-L301; tabela L120-L130 | Inferida (parcial) | RN-018 documenta "faixa pela renda per capita, primeira faixa cujo limite ≥ renda" — o código confirma a lógica de seleção, mas os tetos/fatores (300/600/1000/1500 e 1.0→0.40) são magic numbers não documentados e parametrizados no código (RN-017 dizia estarem no DDM PROGRAMA-SOCIAL). |
| 8  | O sistema deverá aplicar um fator de idade: ≥65 anos → 1,15; ≥60 → 1,10; <18 → 1,05; demais → 1,00. | Ubiquitous | CALCBENF.NSN#L204-L216 | Mistério | <!-- mystery: fator idade com magic numbers (65/60/18 e 1.15/1.10/1.05) sem documentação; idade calculada só por diferença de anos (L205-206), imprecisa. --> |
| 9  | O valor do benefício mensal deverá ser calculado como VLR-BASE × FATOR-REGIONAL × FATOR-FAMILIAR × FATOR-RENDA × FATOR-IDADE. | Ubiquitous | CALCBENF.NSN#L222-L223 | Mistério | <!-- mystery: fórmula real é MULTIPLICATIVA de 5 fatores; a RN-013 documenta fórmula ADITIVA (VALOR-BASE + ACRESCIMO-DEPEND × QT-DEPEND). Divergência crítica: o cálculo de produção não corresponde à regra documentada. Impacto financeiro direto. --> |
| 10 | Após o cálculo base, o sistema deverá aplicar o reajuste do programa multiplicando o valor por (1 + FATOR-REAJUSTE). | Event-driven | CALCBENF.NSN#L226 | Inferida | RN-019/020 (reajuste sobre o valor-base). Aqui o reajuste incide sobre o valor já com todos os fatores, não só sobre a base — contraria a intenção da RN-020. |
| 11 | O sistema deverá truncar (não arredondar) todos os valores monetários para 2 casas decimais. | Ubiquitous | CALCBENF.NSN#L229-L231; L262-L263; L277-L278 | Confirmada | RN-014 (truncamento, não arredondamento matemático). Técnica: × 100 em inteiro N11 e ÷ 100. |
| 12 | Quando o mês da competência for 12 (dezembro), o sistema deverá marcar o pagamento como tipo `D` e somar um 13º calculado como VLR-BASE × FATOR-REGIONAL × FATOR-IDADE ao valor bruto. | Event-driven | CALCBENF.NSN#L240-L246 | Mistério | <!-- mystery: comentário (L237) diz que a fórmula do 13º é VLR_BASE × FATOR_REG × (MESES_ATIVOS/12), mas o código usa VLR_BASE × FATOR_REG × FATOR_IDADE — sem proporcionalidade por meses ativos. Discrepância comentário vs código. RN-013/NOTA confirma "cálculo especial de dezembro". --> |
| 13 | Quando for dezembro e o programa for do tipo `A`, o sistema deverá somar um abono natalino de 15% do benefício mensal ao valor bruto; caso contrário, o abono é zero. | Event-driven | CALCBENF.NSN#L249-L258 | Confirmada (parcial) | Doc cita "abono natalino" (alteração 2009) mas o percentual de 15% e a restrição ao tipo `A` são magic numbers não documentados. |
| 14 | O sistema deverá calcular um desconto interno de 3% sobre o bruto quando este for maior que 500,00 (cálculo simplificado). | State-driven | CALCBENF.NSN#L305-L314 (subrotina CALC-DESCONTOS) | Mistério | <!-- mystery: este desconto de 3% para bruto>500 conflita com CALCDSCT.NSN, que calcula contribuição progressiva por faixa (3/5/7/9%). Dois programas calculam o mesmo desconto de formas diferentes; qual é a fonte da verdade? Comentário L304 admite "VER CALCDSCT P/ COMPLETO". --> |
| 15 | Se o valor líquido (bruto − desconto) resultar negativo, então o sistema deverá ajustá-lo para zero. | Unwanted | CALCBENF.NSN#L271-L274 | Inferida | Proteção contra líquido negativo. |
| 16 | Quando o cálculo for concluído, o sistema deverá gravar o pagamento com status `G` (gerado) e o tipo de pagamento apurado. | Event-driven | CALCBENF.NSN#L281-L291 | Inferida | STORE em PAGAMENTO (ARQ 160). Status `G` não consta no glossário de status de pagamento. |

> **Resumo CALCBENF.NSN:** 16 candidatas — 3 confirmadas (1 parcial), 5 inferidas (1 parcial), 6 mistérios.
> Achados de maior risco: **fórmula real multiplicativa de 5 fatores diverge da RN-013 aditiva**
> (impacto financeiro direto); 13º com fórmula diferente da comentada; desconto de 3% conflitante
> com CALCDSCT; tabelas regionais/idade/renda cheias de magic numbers não documentados; região 99
> recebe fator neutro (relacionado ao "bypass" da RN-005).

## Regras de CALCCORR.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (192 linhas):
> `01-arqueologia/legado-sifap/natural-programs/CALCCORR.NSN`.
> Cross-reference com `legacy-docs/REGRAS-NEGOCIO-2012.md` (RN-019/020 — reajustes/correções).
> **Vocabulário (DEFINE DATA, L11-49):** view `PAGAMENTO-V` (VLR-BRUTO, COMPETENCIA, VLR-CORRECAO,
> DT-CORRECAO, IND-CORRIGIDO A1). Tabelas `#ANO-TAB(10)` e `#IPCA-ANO(10,12)` — índices IPCA
> mensais por ano. Cabeçalho: 2001 (criação), 2006 (novos índices IPCA), 2014 (ajuste período).

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | Se a competência inicial for maior que a competência final, então o sistema deverá rejeitar com "PERIODO INVALIDO". | Unwanted | CALCCORR.NSN#L118-L121 | Inferida | Validação de período. |
| 2  | O sistema deverá processar apenas os pagamentos do CPF informado cuja competência esteja entre a inicial e a final (inclusive), encerrando a leitura quando ultrapassar o período ou mudar de CPF. | Event-driven | CALCCORR.NSN#L126-L138 | Inferida | Controle de leitura sequencial (ESCAPE TOP/BOTTOM) sobre o descritor CPF-BENEF. |
| 3  | Se o pagamento já estiver marcado como corrigido (IND-CORRIGIDO = `S`), então o sistema deverá ignorá-lo (não recorrigir). | Unwanted | CALCCORR.NSN#L140-L142 | Inferida | Idempotência: evita correção em duplicidade. |
| 4  | O sistema deverá corrigir o valor bruto acumulando o índice IPCA mês a mês do período, aplicando VLR-CORRIGIDO = VLR-ORIGINAL × ÍNDICE-ACUMULADO. | Ubiquitous | CALCCORR.NSN#L150-L156; subrotina L172-L188 | Confirmada (parcial) | RN-019 (reajuste por índice). A tabela IPCA está carregada apenas para 2010–2012 (L55-L97), apesar do comentário "última carga: 2014". |
| 5  | Quando a competência do pagamento pertencer a um ano ausente da tabela IPCA, o sistema deverá acumular índice 1,0 (nenhuma correção), silenciosamente. | Unwanted | CALCCORR.NSN#L180-L186 | Mistério | <!-- mystery: a subrotina CALC-INDICE-ACUM só aplica índice se o ano existe em #ANO-TAB (2010-2012); para qualquer outro ano não há ELSE — o índice permanece 1,0 e a correção é zero, sem aviso. Pagamentos fora de 2010-2012 não são corrigidos silenciosamente. --> |
| 6  | O sistema deverá truncar o valor corrigido para 2 casas decimais e calcular a diferença em relação ao valor original. | Ubiquitous | CALCCORR.NSN#L152-L156 | Confirmada | RN-014 (truncamento). |
| 7  | O sistema deverá aplicar a correção (gravar VLR-CORRECAO, DT-CORRECAO e marcar IND-CORRIGIDO = `S`) somente quando a diferença for positiva. | Unwanted | CALCCORR.NSN#L158-L167 | Inferida | Correções negativas (índice deflacionário) nunca são gravadas — só acréscimos. Não há estorno/restituição. |
| 8  | (Bloco histórico desativado) Correção do Plano Verão para competências 01/1989–01/1991 com fatores 2,7500 e 1,4289. | (n/a) | CALCCORR.NSN#L98-L110 | Mistério | <!-- mystery: bloco inteiro comentado ("NAO REMOVER - HISTORICO"), responsável João Batista 2003. Lógica de transição Cruzado→Cruzeiro com magic numbers 2.75/1.4289. Está morto, mas indica que correções de moedas antigas já existiram — investigar se ainda é necessário. --> |

> **Resumo CALCCORR.NSN:** 8 candidatas — 2 confirmadas (1 parcial), 4 inferidas, 2 mistérios.
> Achados de maior risco: tabela IPCA incompleta (só 2010-2012) faz correções silenciosamente
> nulas para outros anos; correções só para diferenças positivas (nunca estorno); bloco "Plano
> Verão" morto com magic numbers.

## Regras de CALCDSCT.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (204 linhas):
> `01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN`.
> Cross-reference com `legacy-docs/REGRAS-NEGOCIO-2012.md` e o exemplo `BR-013` deste catálogo.
> **Vocabulário (DEFINE DATA, L11-53):** view `PAGAMENTO-V` (VLR-BRUTO, VLR-DESCONTO) e
> `BENEFICIARIO-V` com grupo periódico `DESCONTOS (PE)` — TIPO-DSCT (A1: `C=CONTRIB I=IMPOSTO
> J=JUDICIAL S=SINDICAL P=PENSAO A=ADMIN`), VLR-DSCT, PCT-DSCT, DT-INICIO/FIM-DSCT, NUM-PROCESSO.
> Tabela `#FAIXA-CONTRIB(4)` + `#ALIQ-CONTRIB(4)`. Cabeçalho: 1999 (criação), 2007 (desc judicial),
> 2015 (novas alíquotas).

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | Se não existir pagamento com o número informado vinculado ao CPF, então o sistema deverá rejeitar com "PAGAMENTO NAO ENCONTRADO". | Unwanted | CALCDSCT.NSN#L74-L86 | Inferida | Pré-condição. |
| 2  | Se não existir beneficiário com o CPF informado, então o sistema deverá rejeitar com "BENEFICIARIO NAO ENCONTRADO". | Unwanted | CALCDSCT.NSN#L90-L96 | Inferida | Usa `*NUMBER(BENEFICIARIO-V) = 0`. |
| 3  | O sistema deverá calcular uma contribuição social obrigatória por faixa de valor bruto: ≤500→3%; ≤1000→5%; ≤2000→7%; resto→9%. | Ubiquitous | CALCDSCT.NSN#L100; subrotina L188-L200; tabela L57-L65 | Mistério | <!-- mystery: alíquotas progressivas (3/5/7/9%) e faixas (500/1000/2000) são magic numbers sem doc; alteração 2015 "NOVAS ALIQUOTAS". Conflita com CALCBENF que aplica 3% fixo para bruto>500. --> |
| 4  | O sistema deverá definir um teto de desconto igual a 30% do valor bruto. | Ubiquitous | CALCDSCT.NSN#L103-L107 | Confirmada | BR-013. Teto truncado para 2 casas. |
| 5  | O sistema deverá ignorar descontos do grupo periódico que estejam fora de vigência (DT-FIM preenchida e anterior a hoje, ou DT-INICIO futura). | State-driven | CALCDSCT.NSN#L114-L121 | Inferida | Vigência por janela de datas no PE. |
| 6  | Para desconto judicial (`J`), o sistema deverá usar valor fixo (se informado) ou percentual sobre o bruto, e somá-lo SEM aplicar o teto de 30%. | Event-driven | CALCDSCT.NSN#L127-L136; L166-L171 | Confirmada | BR-013 / RN: tipo `J` é exceção legal ao teto. |
| 7  | Para pensão alimentícia (`P`), o sistema deverá usar valor fixo ou percentual sobre o bruto e somá-lo ao total. | Event-driven | CALCDSCT.NSN#L137-L145 | Mistério | <!-- mystery: pensão alimentícia ('P') NÃO é excluída do teto de 30% (só 'J' é). Juridicamente pensão também costuma ser impenhorável/sem teto; o código a submete ao teto. Possível defeito ou regra deliberada não documentada. --> |
| 8  | Para imposto retido (`I`), o sistema deverá calcular percentual sobre o bruto e somá-lo ao total. | Event-driven | CALCDSCT.NSN#L146-L150 | Inferida | Apenas percentual (sem opção de valor fixo). |
| 9  | Para desconto sindical (`S`), o sistema deverá aplicar 1% do bruto. | Event-driven | CALCDSCT.NSN#L151-L154 | Inferida | Magic number 0,01 (1%) hardcoded, sem doc. |
| 10 | Para desconto administrativo (`A`), o sistema deverá usar valor fixo ou percentual sobre o bruto e somá-lo ao total. | Event-driven | CALCDSCT.NSN#L155-L164 | Inferida | Tipo `A` (admin). |
| 11 | Para tipos de desconto não reconhecidos, o sistema deverá ignorá-los. | Unwanted | CALCDSCT.NSN#L165 (NONE / IGNORE) | Inferida | Tipo `C` (contrib) listado no comentário mas não tratado no DECIDE — cai em NONE/IGNORE (já é calculado à parte na subrotina). |
| 12 | Enquanto o tipo de desconto não for `J`, o sistema deverá limitar o total acumulado de descontos a 30% do valor bruto. | State-driven | CALCDSCT.NSN#L166-L171 | Confirmada | BR-013. Aplicado a cada iteração; a ordem de processamento dos descontos no PE afeta o resultado final (efeito de ordem). |
| 13 | Ao final, o sistema deverá truncar o total de descontos e gravá-lo em PAGAMENTO.VLR-DESCONTO. | Event-driven | CALCDSCT.NSN#L174-L185 | Inferida | UPDATE em PAGAMENTO. |

> **Resumo CALCDSCT.NSN:** 13 candidatas — 3 confirmadas, 8 inferidas, 2 mistérios.
> Achados de maior risco: pensão (`P`) submetida ao teto de 30% (só `J` é exceção) — possível erro
> jurídico; alíquotas de contribuição (3/5/7/9%) conflitam com os 3% fixos de CALCBENF; sindical 1%
> hardcoded; efeito de ordem na aplicação do teto.

## Regras de VALELEG.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (~244 linhas):
> `01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN`.
> Cross-reference com `legacy-docs/REGRAS-NEGOCIO-2012.md` (seção 4 — Elegibilidade, RN-015/016) e
> `legacy-docs/MANUAL-TECNICO-SIFAP-2008.md` (3.x). **Resolve vários mistérios em aberto** (região 99,
> significado dos status `S`/`C`/`D`/`I`).
> **Vocabulário (DEFINE DATA, L12-57):** views `BENEFICIARIO-V` (STATUS, COD-REGIAO, RENDA-FAMILIAR,
> NUM-DEPENDENTES, NIS, DOCUMENTOS-OK) e `PROGRAMA-V` (TIPO, COD-ELEGIBILIDADE A5, STATUS-PROG,
> RENDA-MAX, IDADE-MIN, IDADE-MAX) + `#ELEGIVEL (L)`, `#MOTIVO(A60/10)`. Cabeçalho: 1999 (criação),
> 2004 (novas regras eleg), 2009 (ajuste faixa etária), **2013 ("INC REGIAO 99")**.

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | Se não existir beneficiário com o CPF informado, então o sistema deverá rejeitar com "BENEFICIARIO NAO ENCONTRADO". | Unwanted | VALELEG.NSN#L81-L84 | Inferida | Pré-condição. |
| 2  | Se não existir programa com o código informado, então o sistema deverá rejeitar com "PROGRAMA NAO ENCONTRADO". | Unwanted | VALELEG.NSN#L94-L97 | Inferida | Pré-condição. |
| 3  | Se o programa não estiver com status `A`, então o sistema deverá rejeitar com "PROGRAMA INATIVO". | Unwanted | VALELEG.NSN#L99-L102 | Confirmada | RN-003 (apenas programas ativos). |
| 4  | Se o beneficiário for da região 99, então o sistema deverá considerá-lo elegível imediatamente, ignorando todas as demais verificações (status, idade, renda, tipo, documentos). | Unwanted | VALELEG.NSN#L107-L111 | Mistério | <!-- mystery: RESOLVE o "bypass do Roberto" da RN-005 — região 99 é rotulada "INTERNACIONAL/DIPLOMATICO" e curto-circuita TODA a validação de elegibilidade via ESCAPE ROUTINE. Incluída em 05/04/2013 ("INC REGIAO 99", Anderson Lima). Risco de segurança/fraude: qualquer registro com COD-REGIAO=99 passa sem checagem. --> |
| 5  | Se o status do beneficiário não for `A`, o sistema deverá marcá-lo inelegível com o motivo correspondente: `S`→"SUSPENSO", `C`/`D`→"CANCELADO/DESLIGADO", `I`→"INATIVO". | State-driven | VALELEG.NSN#L116-L134 | Confirmada (parcial) | **RESOLVE o significado dos status** (vistos como mistério em CADBENEF/CADDEPEND): A=ativo, S=suspenso, C=cancelado, D=desligado, I=inativo. Apenas `A` é elegível. Cross-ref MYS-002/MYS-004. |
| 6  | Quando o programa define idade mínima (>0), o sistema deverá rejeitar beneficiário com idade inferior; quando define idade máxima (>0), deverá rejeitar idade superior. | State-driven | VALELEG.NSN#L139-L152 | Confirmada | RN (faixa etária por programa). Idade calculada só por diferença de anos (L72-73) — imprecisa. |
| 7  | Quando o programa define renda máxima (>0), o sistema deverá marcar inelegível o beneficiário cuja renda familiar a exceda. | State-driven | VALELEG.NSN#L157-L163 | Confirmada | RN-018 (teto de renda por programa). |
| 8  | O sistema deverá aplicar regras de elegibilidade por tipo de programa: `A` (assistencial) → renda > 600 sem dependentes é inelegível E exige documentação completa (`DOCUMENTOS-OK='S'`); `P` (previdenciário) → idade ≥ 60; `T` (trabalho) → idade entre 16 e 65; tipo desconhecido → inelegível. | Event-driven | VALELEG.NSN#L168-L201 | Confirmada (parcial) | Resolve regras de elegibilidade por tipo (antes ausentes em CADPROG). Magic number 600,00 (assistencial) e faixas etárias (60, 16-65) sem doc formal. |
| 9  | Quando o programa tiver código de elegibilidade específico, o sistema deverá: se o 1º caractere for `R`, exigir NIS cadastrado (≠0); se o 2º caractere for `D`, exigir ao menos 1 dependente. | Optional | VALELEG.NSN#L206-L207; subrotina L223-L242 | Mistério | <!-- mystery: COD-ELEGIBILIDADE (A5) é um código posicional críptico — cada caractere liga uma regra (pos1='R'→NIS, pos2='D'→dependentes). Só 2 das 5 posições são interpretadas; significado das posições 3-5 é desconhecido. Documentação (RN-015) está PENDENTE. --> |

> **Resumo VALELEG.NSN:** 9 candidatas — 5 confirmadas (2 parciais), 2 inferidas, 2 mistérios.
> **Resolve mistérios pré-existentes:** região 99 (bypass diplomático que pula toda validação) e o
> domínio de status A/S/C/D/I. Novo achado crítico de segurança: COD-REGIAO=99 = elegibilidade
> automática. Código de elegibilidade posicional (A5) permanece parcialmente indecifrado.

## Regras de VALBENEF.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (~290 linhas):
> `01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN`.
> Cross-reference com `legacy-docs/REGRAS-NEGOCIO-2012.md` (RN-001) e Manual 3.x.
> **Vocabulário (DEFINE DATA, L11-66):** view `BENEFICIARIO-V` (CPF, NOME, DT-NASCIMENTO, SEXO, UF,
> CEP, STATUS) + `#RESULTADO (V/I)`, `#MSG-ERRO(A60/10)`, tabelas `#UF-TAB(27)`, `#DIAS-MES(12)` e
> variáveis do algoritmo de CPF. Cabeçalho: 1998 (criação), 2005 (ajuste valid CPF), 2010 (inc valid
> nome). Subrotinas: `VALIDA-CPF-COMPLETO`, `VALIDA-DATA`, `VALIDA-NOME`.

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | O sistema deverá validar o CPF pelo dígito verificador (módulo 11); se inválido, registrar "CPF INVALIDO - DIGITO VERIFICADOR". | Unwanted | VALBENEF.NSN#L119-L124; subrotina L195-L255 | Confirmada | RN-001. Algoritmo mod-11 padrão (pesos 10→2 e 11→2). |
| 2  | Se todos os 11 dígitos do CPF forem iguais, o CPF é inválido — EXCETO quando começa com `000`, caso em que é considerado válido (CPF de teste do governo). | Unwanted | VALBENEF.NSN#L210-L223 | Mistério | <!-- mystery: exceção que aceita CPFs com todos os dígitos iguais iniciados em '000' como válidos ("TESTE GOVERNO"). Backdoor de teste em produção — pode permitir cadastros fictícios. Não documentado. Relaciona-se com a lista de prefixos especiais de VALDOCS. --> |
| 3  | O sistema deverá validar a data de nascimento: ano entre 1900 e o ano atual, mês entre 1 e 12, e dia entre 1 e o número de dias do mês. | Unwanted | VALBENEF.NSN#L132-L137; subrotina L259-L277 | Inferida | RN-006 (DT-NASC). |
| 4  | O sistema deverá aceitar 29 de fevereiro em qualquer ano. | Ubiquitous | VALBENEF.NSN#L106-L107 (tabela #DIAS-MES); L271-L274 | Mistério | <!-- mystery: #DIAS-MES(2)=29 fixo ("CONSIDERA BISSEXTO"), mas NÃO há cálculo de ano bissexto — 29/02 é aceito mesmo em anos não bissextos. Defeito de validação. --> |
| 5  | O sistema deverá exigir que o nome contenha ao menos um espaço a partir da 2ª posição (nome + sobrenome); caso contrário, "NOME INVALIDO". | Unwanted | VALBENEF.NSN#L143-L148; subrotina L281-L295 | Confirmada | Alteração 2010 ("INC VALID NOME"). |
| 6  | Quando a UF for informada, o sistema deverá validá-la contra a tabela das 27 UFs; caso contrário, "UF INVALIDA". | Unwanted | VALBENEF.NSN#L153-L171 | Inferida | Tabela de 27 UFs (L70-L96). |
| 7  | O sistema deverá rejeitar status que não seja `A`, `S`, `C`, `I` ou `D` com "STATUS INVALIDO". | Unwanted | VALBENEF.NSN#L176-L182 | Confirmada | **Define o domínio fechado de status A/S/C/I/D** — confirma o conjunto visto em VALELEG. |

> **Resumo VALBENEF.NSN:** 7 candidatas — 4 confirmadas, 2 inferidas, 2 mistérios.
> Achados de maior risco: backdoor de CPF de teste (`000` + dígitos iguais aceito); 29/02 aceito em
> ano não bissexto. Confirma o domínio de status A/S/C/I/D.

## Regras de VALDOCS.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (~200 linhas):
> `01-arqueologia/legado-sifap/natural-programs/VALDOCS.NSN`.
> Cross-reference com Manual 3.x (validação de documentação comprobatória).
> **Vocabulário (DEFINE DATA, L11-37):** view `BENEFICIARIO-V` (CPF, RG, DOCUMENTOS-OK) + entradas
> `#TITULO`, `#CTPS`, `#RESULTADO`, tabela `#PREF-ESP(8)` (prefixos especiais de CPF). Cabeçalho:
> 1998 (criação), 2003 (inc valid RG), 2011 (ajuste check espec). Subrotinas: `VALIDA-CPF-DOC`,
> `VALIDA-RG`, `CHECK-DOC-ESPECIAL`.

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | Se o CPF for zero ou não passar no dígito verificador (módulo 11), então o sistema deverá registrar "CPF INVALIDO". | Unwanted | VALDOCS.NSN#L74-L79; subrotina L116-L160 | Confirmada | RN-001. Mesmo algoritmo mod-11. |
| 2  | Se o RG estiver em branco ou tiver menos de 5 caracteres, então o sistema deverá registrar "RG INVALIDO OU FORMATO INCORRETO". | Unwanted | VALDOCS.NSN#L84-L89; subrotina L164-L182 | Inferida | Alteração 2003 ("INC VALID RG"). Tamanho mínimo 5 é magic number. |
| 3  | Se os 3 primeiros dígitos do CPF estiverem na lista de prefixos especiais (000, 001, 002, 010, 011, 099, 100, 999), então o sistema deverá marcar o documento como válido, forçar CPF válido e zerar todos os erros de validação. | Unwanted | VALDOCS.NSN#L93; subrotina L186-L205; tabela L41-L48 | Mistério | <!-- mystery: BACKDOOR — CHECK-DOC-ESPECIAL sobrescreve TODO o resultado da validação (RESULTADO='V', CPF-OK=TRUE, QTD-ERROS=0) para CPFs com prefixos "governo/teste". Inclui prefixos 099/100/999 sem explicação. Risco de segurança grave: documentos inválidos passam se o CPF começar com um prefixo da lista. Ajustado em 2011 (Roberto Mendes). --> |

> **Resumo VALDOCS.NSN:** 3 candidatas — 1 confirmada, 1 inferida, 1 mistério.
> Achado de maior risco: backdoor de prefixos especiais de CPF que anula toda a validação de
> documentos — vetor de fraude. Relaciona-se com a exceção `000` de VALBENEF.

## Regras de BATCHPGT.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (~340 linhas):
> `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN`.
> Cross-reference com `legacy-docs/REGRAS-NEGOCIO-2012.md` (seção 5 — Batch de Pagamento).
> **Vocabulário (DEFINE DATA, L15-105):** views `BENEFICIARIO-V`, `PAGAMENTO-V`, `PROGRAMA-V` +
> contadores (`#QTD-PROCESSADOS/GERADOS/IGNORADOS/ERROS`), acumuladores `N13.2`, e **cópia das
> tabelas de CALCBENF** (`#TAB-REG(27)`, `#FAIXA-RENDA(5)`, `#FATOR-FAIXA(5)`). Cabeçalho registra 6
> alterações (2000 ord CPF, 2004 log erros, 2009 13º/abono, 2012 novas faixas, 2015 auditoria).

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | O processamento mensal deverá ler os beneficiários em ordem de CPF; sistemas a jusante dependem dessa ordenação. | Ubiquitous | BATCHPGT.NSN#L196-L201 | Confirmada | RN (5.1 "ordenação padrão"). Comentário explícito: "SISTEMAS DOWNSTREAM DEPENDEM DESTA ORDENACAO" — restrição de integração a preservar na migração. |
| 2  | Se o CPF do registro for igual ao do registro anterior, o sistema deverá ignorá-lo como duplicata. | Unwanted | BATCHPGT.NSN#L205-L210 | Mistério | <!-- mystery: dedup só compara com o CPF IMEDIATAMENTE anterior (#CPF-ANT). Como a leitura é por CPF, duplicatas não adjacentes escapam. Por que haveria CPFs duplicados no arquivo de beneficiários (chave única)? Indício de dado sujo. --> |
| 3  | O sistema deverá processar somente beneficiários com status `A` (ativo); os demais são ignorados. | State-driven | BATCHPGT.NSN#L213-L216 | Confirmada | RN-021 / seção 5.1 (apenas ativos). |
| 4  | Se já existir pagamento do beneficiário na competência corrente, o sistema deverá ignorá-lo (não regerar). | Unwanted | BATCHPGT.NSN#L219-L228 | Inferida | Idempotência do ciclo mensal. |
| 5  | Se o programa do beneficiário não for encontrado, o sistema deverá registrar erro em log e pular o registro; se o programa estiver inativo, deverá ignorá-lo. | Unwanted | BATCHPGT.NSN#L231-L246 | Inferida | Log de erros (alteração 2004). |
| 6  | O sistema deverá calcular o valor do benefício (fatores regional/familiar/renda/idade, reajuste, 13º, abono e desconto) com a mesma lógica de CALCBENF. | Event-driven | BATCHPGT.NSN#L249-L310 | Mistério | <!-- mystery: o cabeçalho diz "CHAMA CALCBENF E CALCDSCT", mas o batch NÃO faz CALLNAT — ele REIMPLEMENTA inline toda a fórmula de CALCBENF (tabelas e fatores duplicados). Dois cálculos independentes do mesmo benefício = risco de divergência quando um for alterado e o outro não. Fonte da verdade ambígua. --> |
| 7  | Quando o mês for dezembro, o sistema deverá calcular 13º (VLR-BASE × FATOR-REG × FATOR-IDADE) e, para programa tipo `A`, abono de 15%. | Event-driven | BATCHPGT.NSN#L283-L294 | Confirmada (parcial) | Idêntico a CALCBENF #12/#13 (mesmos magic numbers, mesma divergência do comentário sobre meses ativos). |
| 8  | O desconto deverá ser 3% do bruto quando este exceder 500,00 (cálculo simplificado embutido). | State-driven | BATCHPGT.NSN#L297-L303 | Mistério | <!-- mystery: o batch aplica o desconto simplificado de 3% (igual à subrotina de CALCBENF), NÃO o cálculo progressivo/completo de CALCDSCT (3/5/7/9% + descontos do PE group). O cabeçalho promete CALCDSCT, mas o desconto real do pagamento mensal é o simplificado. Beneficiários podem ser descontados a menor. --> |
| 9  | Cada pagamento gerado deverá receber número sequencial (último + 1), status `G` (gerado) e a competência corrente. | Event-driven | BATCHPGT.NSN#L306-L322 | Inferida | Sequência obtida via READ DESCENDING do último NUM-PAGTO (L181-L184). |
| 10 | O sistema deverá acumular totais (bruto, desconto, líquido, abono) e produzir um resumo com processados, gerados, ignorados e erros. | Ubiquitous | BATCHPGT.NSN#L324-L345 | Inferida | Saída de controle do batch. |

> **Resumo BATCHPGT.NSN:** 10 candidatas — 3 confirmadas (1 parcial), 4 inferidas, 3 mistérios.
> Achados de maior risco: **reimplementação inline do cálculo de CALCBENF** (header promete CALLNAT)
> — dupla fonte da verdade; desconto simplificado de 3% em vez do CALCDSCT prometido; dedup frágil
> só de CPF adjacente; dependência de ordenação por CPF para sistemas a jusante.

## Regras de BATCHCON.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (~300 linhas):
> `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN`.
> Cross-reference com Manual 3.x (conciliação CNAB) e seção 5 da RN-2012.
> **Vocabulário (DEFINE DATA, L15-95):** views `PAGAMENTO-V` (STATUS-PGTO, DT-PAGAMENTO, COD-BANCO,
> COD-RETORNO) e `AUDITORIA-V` (SEQ-AUDIT, USUARIO, ACAO, VLR-ANTERIOR/NOVO). Registro CNAB 240
> (`#REG-CNAB A240`) parseado por SUBSTR. Cabeçalho: 2000 (criação), 2005 (banco Real), 2008 (CNAB
> 240), 2014 (auditoria). Subrotinas: `GRAVA-AUDITORIA-CONC`, `GRAVA-AUDITORIA-DIVERG`.

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | O sistema deverá processar somente registros de detalhe do arquivo CNAB 240 (tipo de registro = `3`), ignorando os demais. | Unwanted | BATCHCON.NSN#L120-L123 | Inferida | Layout CNAB 240 BB (alteração 2008). Posições de parse hardcoded (CPF 44-54, valor 120-134, etc.). |
| 2  | Para cada registro de detalhe, o sistema deverá localizar o pagamento por número do documento + CPF + competência; se não encontrado, contabilizar "NAO ENCONTRADO". | Unwanted | BATCHCON.NSN#L141-L160 | Inferida | Conciliação por chave tripla. |
| 3  | Se a diferença absoluta entre o valor líquido do SIFAP e o valor retornado pelo banco for maior que R$ 0,01, o sistema deverá marcar divergência e gravar registro de auditoria. | Unwanted | BATCHCON.NSN#L163-L178 | Confirmada | Auditoria (alteração 2014). Tolerância de 1 centavo (magic number). |
| 4  | Quando conciliado, o sistema deverá atualizar o status do pagamento conforme o código de retorno bancário: `00` → `P` (pago, grava data e banco), `01` → `D` (devolvido), `02` → `E` (estornado); código desconhecido é logado. | Event-driven | BATCHCON.NSN#L180-L213 | Confirmada | **Define os status de pagamento** P/D/E e o significado dos códigos de retorno CNAB. COD-BANCO é gravado fixo como `1`. |
| 5  | O sistema deverá gravar um registro de auditoria para cada pagamento conciliado e para cada divergência (usuário `BATCH`, ação `CO`/`DV`). | Event-driven | BATCHCON.NSN#L215-L223; subrotinas L237-L290 | Confirmada | RN de auditoria; rastreabilidade de conciliação. |
| 6  | (Bloco histórico desativado) Integração com retorno do Banco Real (cód. 356), com layout distinto do BB. | (n/a) | BATCHCON.NSN#L226-L242 | Mistério | <!-- mystery: bloco inteiro comentado ("BANCO REAL - DESCONTINUADA", adquirido pelo Santander em 2007). Código morto mantido "para referência histórica". Indica que houve suporte multi-banco com layouts diferentes — relevante se a migração precisar reconciliar arquivos antigos. --> |

> **Resumo BATCHCON.NSN:** 6 candidatas — 3 confirmadas, 2 inferidas, 1 mistério.
> **Resolve os status de pagamento** (P=pago, D=devolvido, E=estornado via códigos CNAB 00/01/02).
> Achados de maior risco: posições CNAB e COD-BANCO=1 hardcoded; bloco morto do Banco Real;
> tolerância de conciliação de 1 centavo.

## Regras de BATCHREL.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (~250 linhas):
> `01-arqueologia/legado-sifap/natural-programs/BATCHREL.NSN`.
> Cross-reference com Manual 3.x (relatórios gerenciais).
> **Vocabulário (DEFINE DATA, L11-66):** views `PAGAMENTO-V`, `BENEFICIARIO-V` (COD-REGIAO) +
> acumuladores por região (5), por status (5) e gerais. Cabeçalho: 1999 (criação), 2006 (subtotais
> região), 2013 (ajuste formato). Subrotina: `IMPRIME-CABECALHO`.

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | O sistema deverá consolidar os pagamentos de uma competência agrupando-os em 5 macro-regiões a partir do código de região: 1–5→Norte, 6–10→Nordeste, 11–15→Sudeste, 16–20→Sul, demais→Centro-Oeste. | Ubiquitous | BATCHREL.NSN#L138-L155 | Mistério | <!-- mystery: o mapeamento código-de-região → macro-região é hardcoded e o ELSE joga TUDO que estiver fora de 1-20 em "Centro-Oeste", incluindo região 99 (diplomático) e códigos 21-25. Distorce o relatório gerencial. Não confere com a tabela de 27 UFs de VALBENEF. --> |
| 2  | O sistema deverá arredondar (não truncar) os valores brutos no relatório, somando 0,005 antes de truncar. | Ubiquitous | BATCHREL.NSN#L158-L163 | Mistério | <!-- mystery: comentário explícito "ARREDONDAMENTO DIFERE DO CALCBENF (ROUND VS TRUNCATE)". O relatório ARREDONDA, mas o pagamento TRUNCA (RN-014). Logo os totais do relatório gerencial NÃO batem com a soma dos pagamentos — divergência sistemática de conciliação contábil. --> |
| 3  | O sistema deverá consolidar os pagamentos por status: `G`→Gerado, `P`→Pago, `C`→Cancelado, `D`→Devolvido, `E`→Estornado; status desconhecido conta como Gerado. | Ubiquitous | BATCHREL.NSN#L176-L195 | Confirmada | **Confirma o domínio de status de pagamento** G/P/C/D/E (complementa BATCHCON). O fallback NONE→Gerado pode mascarar status inválidos. |
| 4  | O sistema deverá produzir totais gerais (quantidade, bruto, desconto, líquido) e paginar a saída a cada 66 linhas (padrão de impressora mainframe). | Ubiquitous | BATCHREL.NSN#L197-L235; subrotina L238-L250 | Inferida | Detalhe de apresentação; 66 linhas/página é parâmetro de impressora. |

> **Resumo BATCHREL.NSN:** 4 candidatas — 1 confirmada, 1 inferida, 2 mistérios.
> Achados de maior risco: **relatório arredonda enquanto pagamento trunca** (totais gerenciais não
> conciliam com pagamentos); bucketing de macro-região joga códigos 21-25 e região 99 em
> "Centro-Oeste". Confirma domínio de status de pagamento G/P/C/D/E.

## Regras de CONSBENF.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (~200 linhas):
> `01-arqueologia/legado-sifap/natural-programs/CONSBENF.NSN`.
> Cross-reference com Manual 3.x (consulta online 3270). **Relevante para LGPD** (mascaramento de CPF).
> **Vocabulário (DEFINE DATA, L13-72):** views `BENEFICIARIO-V` e `PAGAMENTO-V`, busca por
> `#TIPO-BUSCA (C/N)`, máscara `#CPF-MASK`, histórico `#HIST-*(/12)`. Cabeçalho: 1998 (criação),
> 2003 (inc máscara CPF), 2007 (hist pagtos), 2012 (tela MAP). Subrotina: `MASCARA-CPF`.

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | A consulta poderá ser feita por CPF (`C`) ou por NIS (`N`); na ausência de tipo, assume `C`; tipo inválido é rejeitado com "TIPO BUSCA INVALIDO". | Event-driven | CONSBENF.NSN#L90-L108 | Inferida | Default `C` (L86-88). |
| 2  | Se o beneficiário não for encontrado, o sistema deverá informar "BENEFICIARIO NAO ENCONTRADO". | Unwanted | CONSBENF.NSN#L110-L113 | Inferida | Pré-condição. |
| 3  | O sistema deverá mascarar o CPF exibido no formato `***.***.XXX-XX`, ocultando dados sensíveis. | Ubiquitous | CONSBENF.NSN#L116-L118; subrotina L175-L195 | Mistério | <!-- mystery: a subrotina MASCARA-CPF tem inconsistência CONHECIDA e documentada no código: para CPF com menos de 11 dígitos mostra os 3 PRIMEIROS dígitos em vez de mascará-los, vazando dados. Comentário: "NAO CORRIGIR SEM APROVACAO DA AUDITORIA". Defeito de privacidade/LGPD deliberadamente mantido. --> |
| 4  | O sistema deverá traduzir o status do beneficiário: `A`→ATIVO, `S`→SUSPENSO, `C`→CANCELADO, `I`→INATIVO, `D`→DESLIGADO; desconhecido→DESCONHECIDO. | Ubiquitous | CONSBENF.NSN#L121-L137 | Confirmada | **Confirma o domínio e os rótulos dos status** A/S/C/I/D (alinha com VALELEG/VALBENEF). |
| 5  | O sistema deverá exibir o histórico dos últimos 12 pagamentos do beneficiário, ordenados por leitura do descritor CPF-BENEF. | Event-driven | CONSBENF.NSN#L158-L173 | Inferida | Limite fixo de 12 (alteração 2007). |

> **Resumo CONSBENF.NSN:** 5 candidatas — 1 confirmada, 3 inferidas, 1 mistério.
> Achado de maior risco: mascaramento de CPF com vazamento conhecido (LGPD) mantido por decisão de
> auditoria. Confirma rótulos de status A/S/C/I/D.

## Regras de RELPGT.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (~230 linhas):
> `01-arqueologia/legado-sifap/natural-programs/RELPGT.NSN`.
> Cross-reference com Manual 3.x (relatório de pagamentos por período/programa/UF).
> **Vocabulário (DEFINE DATA, L11-66):** views `PAGAMENTO-V`, `BENEFICIARIO-V` + acumuladores e
> subtotais por programa, controle de paginação (66 linhas). Cabeçalho: 1999 (criação), 2004
> (paginação), 2010 (subtotal por programa). Subrotinas: `IMPRIME-CABECALHO`, `IMPRIME-SUBTOTAL`.

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | O relatório deverá listar pagamentos cuja competência esteja entre a inicial e a final informadas. | Event-driven | RELPGT.NSN#L92-L96 | Inferida | Leitura por descritor COMPETENCIA. |
| 2  | Quando um código de programa for informado (≠0), o sistema deverá filtrar apenas pagamentos desse programa; 0 significa todos. | Optional | RELPGT.NSN#L98-L102 | Inferida | Filtro opcional. |
| 3  | O sistema deverá emitir um subtotal a cada quebra de programa (controle de quebra) e um total geral ao final. | Event-driven | RELPGT.NSN#L104-L112; L170-L182; subrotina L223-L233 | Inferida | Control-break por COD-PROGRAMA (alteração 2010). |
| 4  | O sistema deverá mascarar o CPF no formato `***.NNN.NNN-NN`, ocultando apenas os 3 primeiros dígitos. | Ubiquitous | RELPGT.NSN#L124-L128 | Mistério | <!-- mystery: máscara de CPF aqui é DIFERENTE da de CONSBENF — RELPGT oculta só os 3 primeiros dígitos e expõe os 8 restantes (incl. os últimos). Inconsistência de mascaramento entre programas; expõe mais dados que o esperado. LGPD. --> |
| 5  | O sistema deverá traduzir o tipo de pagamento: `N`→NORMAL, `D`→DECIMO, `T`→TERCEIRO; outros→OUTRO. | Ubiquitous | RELPGT.NSN#L131-L140 | Mistério | <!-- mystery: o domínio de TIPO-PGTO inclui 'T'=TERCEIRO, mas nem CALCBENF nem BATCHPGT geram 'T' (só 'N' e 'D'). Valor de domínio órfão — de onde vem um pagamento tipo 'T'? Processo desconhecido escreve esse tipo. --> |
| 6  | O sistema deverá traduzir o status de pagamento: `G`→GERADO, `P`→PAGO, `C`→CANCELADO, `D`→DEVOLVIDO, `E`→ESTORNADO; outros→OUTRO. | Ubiquitous | RELPGT.NSN#L143-L156 | Confirmada | Confirma domínio de status de pagamento G/P/C/D/E. |

> **Resumo RELPGT.NSN:** 6 candidatas — 1 confirmada, 3 inferidas, 2 mistérios.
> Achados de maior risco: máscara de CPF divergente da de CONSBENF (LGPD); tipo de pagamento `T`
> (terceiro) é valor de domínio órfão sem produtor conhecido.

## Regras de RELAUDIT.NSN

> Extração `/extract-business-rules` em 2026-06-10. Arquivo lido na íntegra (~250 linhas):
> `01-arqueologia/legado-sifap/natural-programs/RELAUDIT.NSN`.
> Cross-reference com Manual 3.x (relatório de auditoria). **Relevante para compliance/segurança.**
> **Vocabulário (DEFINE DATA, L13-55):** view `AUDITORIA-V` (DT-EVENTO, USUARIO, ACAO, TABELA-REF,
> CHAVE-REF, VLR-ANTERIOR/NOVO) + filtros e contadores por ação. Cabeçalho: 2002 (criação), 2006
> (filtros), 2011 (formato), **2014 ("LIMPEZA RELATORIO")**. Subrotina: `IMPRIME-CAB-AUDIT`.

| #   | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|-----|---------------------|----------------|-------|---------------|-------|
| 1  | Na ausência de datas, o sistema deverá assumir período de 01/01/1997 até a data atual; saída padrão é tela (`T`). | Ubiquitous | RELAUDIT.NSN#L92-L102 | Inferida | 19970101 = início do sistema (magic date). |
| 2  | O sistema deverá listar os eventos de auditoria cuja data esteja dentro do período informado. | Event-driven | RELAUDIT.NSN#L106-L114 | Inferida | Leitura por descritor DT-EVENTO. |
| 3  | O sistema NÃO deverá exibir eventos de auditoria cuja ação seja `EX` (exclusão). | Unwanted | RELAUDIT.NSN#L120-L123 | Mistério | <!-- mystery: o relatório de trilha de auditoria FILTRA e oculta eventos de exclusão (ACAO='EX') — exatamente os eventos mais sensíveis para auditoria. Introduzido/mantido na alteração 2014 "LIMPEZA RELATORIO" (Fernanda Costa). Bandeira vermelha de compliance: exclusões ficam invisíveis na trilha. --> |
| 4  | O sistema deverá aplicar filtros opcionais por ação, usuário e tabela quando informados. | Optional | RELAUDIT.NSN#L126-L147 | Inferida | Filtros (alteração 2006). |
| 5  | O sistema deverá classificar e contabilizar cada evento por ação: `IN`→Inclusão, `AL`→Alteração, `CO`→Conciliação, `CN`→Consulta, `DV`→Divergência; demais→Outra. | Ubiquitous | RELAUDIT.NSN#L151-L170 | Confirmada | **Define o domínio de ações de auditoria** (IN/AL/CO/CN/DV + EX oculto). Complementa BATCHCON (CO/DV). |
| 6  | O sistema deverá formatar a hora do evento como HH:MM:SS e paginar a saída (tela ou impressora) a cada 66 linhas. | Ubiquitous | RELAUDIT.NSN#L172-L210; subrotina L233-L258 | Inferida | Apresentação. |

> **Resumo RELAUDIT.NSN:** 6 candidatas — 1 confirmada, 4 inferidas, 1 mistério.
> Achado de maior risco: **exclusões (`EX`) ocultadas da trilha de auditoria** — violação grave de
> compliance. Define o domínio de ações de auditoria IN/AL/CO/CN/DV/EX.

## Resumo Estatístico

> Consolidado da extração `/extract-business-rules` sobre os **15 programas Natural** (todos os `.NSN`).

- **Total de regras encontradas: 125 candidatas** distribuídas em 15 programas:
  CADBENEF 15 · CADDEPEND 9 · CADPROG 8 · CALCBENF 16 · CALCCORR 8 · CALCDSCT 13 · VALELEG 9 ·
  VALBENEF 7 · VALDOCS 3 · BATCHPGT 10 · BATCHCON 6 · BATCHREL 4 · CONSBENF 5 · RELPGT 6 · RELAUDIT 6.
- **Regras críticas (risco financeiro / segurança / LGPD / compliance): ~28.** Destaques:
  fator-K `0.347215` (CADPROG); fórmula multiplicativa de 5 fatores divergente da RN-013 (CALCBENF);
  desconto/teto de 30% e exceção judicial (CALCDSCT); bypass de elegibilidade da região 99 (VALELEG);
  backdoors de CPF `000`/prefixos especiais (VALBENEF/VALDOCS); vazamento de CPF no mascaramento
  (CONSBENF/RELPGT); exclusões ocultadas da trilha de auditoria (RELAUDIT).
- **Regras com duplicação / fonte da verdade ambígua: 5 clusters.**
  (1) Cálculo de benefício em CALCBENF vs reimplementação inline em BATCHPGT;
  (2) desconto 3% fixo (CALCBENF/BATCHPGT) vs progressivo 3/5/7/9% (CALCDSCT);
  (3) algoritmo mod-11 de CPF replicado em CADBENEF, VALBENEF e VALDOCS;
  (4) tabela de fatores regionais duplicada em CALCBENF e BATCHPGT;
  (5) mascaramento de CPF divergente entre CONSBENF e RELPGT.
- **Regras sem documentação (mistérios marcados `<!-- mystery: -->`): 33.**
  Por programa: CADBENEF 3 · CADDEPEND 2 · CADPROG 3 · CALCBENF 6 · CALCCORR 2 · CALCDSCT 2 ·
  VALELEG 2 · VALBENEF 2 · VALDOCS 1 · BATCHPGT 3 · BATCHCON 1 · BATCHREL 2 · CONSBENF 1 ·
  RELPGT 2 · RELAUDIT 1.

> **Mistérios resolvidos durante esta rodada:** o significado dos status de beneficiário
> (`A`/`S`/`C`/`I`/`D` — via VALELEG, VALBENEF, CONSBENF), os status de pagamento
> (`G`/`P`/`C`/`D`/`E` — via BATCHCON, BATCHREL, RELPGT) e o **bypass da região 99** ("do Roberto",
> RN-005 — resolvido em VALELEG: elegibilidade automática diplomática). Recomenda-se re-rodar
> `/catalog-mysteries` para consolidar os 33 mistérios atuais em `mysteries-found.md`.

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="GUIDE.md"><strong>GUIDE do Estágio 1</strong></a><br/>
<sub>Passo a passo do estágio.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="dependency-map.md"><strong>dependency-map.md</strong></a><br/>
<sub>Mapa de quem chama quem.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="README.md">Voltar ao Kit PT-BR</a></sub>

