<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# SPECIFICATION — Sistema Moderno SIFAP

![ESTÁGIO 02 Spec Moderna](https://img.shields.io/badge/ESTÁGIO-02%20Spec%20Moderna-00A4EF?style=for-the-badge) ![NOTAÇÃO EARS](https://img.shields.io/badge/NOTAÇÃO-EARS-1A1A1A?style=for-the-badge) ![RASTREABILIDADE source__legacy](https://img.shields.io/badge/RASTREABILIDADE-source__legacy-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 2](README.md) → **SPECIFICATION**

> **Origem:** `/write-ears-spec` sobre [`business-rules-catalog.md`](../01-arqueologia/business-rules-catalog.md)
> (apenas regras **Confirmadas** / Confirmadas parciais) + [`bounded-contexts.md`](bounded-contexts.md)
> (5 contextos). Mistérios `blocks-stage-2` de [`mysteries-found.md`](../01-arqueologia/mysteries-found.md)
> ficam em **Open Questions** — nunca viram requisito. **Data:** 2026-06-10.
>
> **Regras de geração aplicadas:** somente regras classificadas "Confirmada" (incl. "Confirmada (parcial)")
> foram promovidas a requisito. Nenhuma regra "Inferida" ou "Mistério" virou requisito nesta passada.
> Onde uma regra confirmada tem discrepância documentada (ex.: limite de dependentes 5 vs 3), o requisito
> reflete o **comportamento confirmado no código** e a divergência é sinalizada em **Notas** + Open Questions.
>
> ⚠️ Esta spec aguarda ratificação da equipe. Promoção de regras "Inferidas" exige decisão explícita por regra.

---

## Bounded Context: Cadastro de Beneficiários

> Possui `BENEFICIARIO` (incl. grupo de dependentes). Programas-fonte: CADBENEF, CADDEPEND, VALBENEF, VALDOCS.

### REQ-001: CPF obrigatório no cadastro

- **EARS Pattern:** Unwanted behavior
- **Declaração:** Se o CPF informado for igual a zero, então o sistema deverá rejeitar o cadastro com a mensagem "CPF OBRIGATORIO".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L105-L109`
- **Source Rule:** CADBENEF #2 (Confirmada — RN-001, Manual 3.2.1)
- **Critérios de Aceite:**
  - [ ] Given um cadastro com CPF = 0, when a inclusão é submetida, then o sistema rejeita com "CPF OBRIGATORIO" e nenhum registro é persistido.
  - [ ] Given um cadastro com CPF ≠ 0, when a inclusão é submetida, then a validação de CPF obrigatório não bloqueia o fluxo.

### REQ-002: Cálculo do dígito verificador do CPF (módulo 11)

- **EARS Pattern:** Ubiquitous
- **Declaração:** O sistema deverá calcular o dígito verificador do CPF pelo módulo 11, com peso decrescente 10→2 para o 1º dígito e 11→2 para o 2º dígito; quando o resto da divisão for menor que 2 o dígito é 0, caso contrário é 11 menos o resto.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L224-L269`
- **Source Rule:** CADBENEF #4 (Confirmada — RN-001); replicada em VALBENEF #1, VALDOCS #1
- **Critérios de Aceite:**
  - [ ] Given um CPF base válido conhecido, when os dígitos verificadores são calculados, then o resultado coincide com os DVs esperados do mod-11.
  - [ ] Given resto da divisão < 2, when o dígito é determinado, then o dígito resultante é 0.

### REQ-003: Rejeição de CPF inválido

- **EARS Pattern:** Unwanted behavior
- **Declaração:** Se o CPF informado não passar na validação de dígito verificador (módulo 11), então o sistema deverá rejeitar a operação com a mensagem "CPF INVALIDO".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L111-L117`, `01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN#L119-L124`, `01-arqueologia/legado-sifap/natural-programs/VALDOCS.NSN#L74-L79`
- **Source Rule:** CADBENEF #3, VALBENEF #1, VALDOCS #1 (todas Confirmadas — RN-001)
- **Notas:** A validação mod-11 deve ser um serviço **único** deste contexto (hoje o algoritmo está triplicado em CADBENEF/VALBENEF/VALDOCS). Os backdoors de prefixo (`000`/prefixos especiais) **não** entram aqui — ver Open Questions OQ-007 e OQ-008.
- **Critérios de Aceite:**
  - [ ] Given um CPF com DV incorreto, when a validação executa, then o sistema rejeita com "CPF INVALIDO".
  - [ ] Given um CPF com DV correto, when a validação executa, then o CPF é aceito.

### REQ-004: Data de nascimento obrigatória

- **EARS Pattern:** Unwanted behavior
- **Declaração:** Se a data de nascimento informada for igual a zero, então o sistema deverá rejeitar o cadastro com a mensagem "DATA NASCIMENTO OBRIGATORIA".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L125-L129`
- **Source Rule:** CADBENEF #6 (Confirmada — RN-006)
- **Notas:** RN-006 também exige idade mínima de 16 anos, **não implementada** no legado — ver OQ relacionada à validação etária (fora desta passada de requisitos confirmados).
- **Critérios de Aceite:**
  - [ ] Given data de nascimento = 0, when a inclusão é submetida, then o sistema rejeita com "DATA NASCIMENTO OBRIGATORIA".
  - [ ] Given data de nascimento preenchida, when a inclusão é submetida, then a regra de obrigatoriedade não bloqueia.

### REQ-005: Nome deve conter nome e sobrenome

- **EARS Pattern:** Unwanted behavior
- **Declaração:** Se o nome do beneficiário não contiver ao menos um espaço a partir da 2ª posição (nome seguido de sobrenome), então o sistema deverá rejeitar com a mensagem "NOME INVALIDO".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN#L143-L148`
- **Source Rule:** VALBENEF #5 (Confirmada — alteração 2010 "INC VALID NOME")
- **Critérios de Aceite:**
  - [ ] Given nome "ANA" (sem sobrenome), when validado, then o sistema rejeita com "NOME INVALIDO".
  - [ ] Given nome "ANA SILVA", when validado, then o nome é aceito.

### REQ-006: Status inicial ativo na inclusão

- **EARS Pattern:** Event-driven
- **Declaração:** Quando um beneficiário for incluído, o sistema deverá atribuir o status inicial `A` (ativo).
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L161-L164`
- **Source Rule:** CADBENEF #10 (Confirmada — RN-002/RN-011)
- **Critérios de Aceite:**
  - [ ] Given uma inclusão válida, when o registro é persistido, then o status gravado é `A`.
  - [ ] Given uma consulta ao registro recém-incluído, when o status é lido, then retorna `A`.

### REQ-007: Bloqueio de CPF já cadastrado na inclusão

- **EARS Pattern:** Unwanted behavior
- **Declaração:** Se a operação for inclusão e já existir beneficiário com o mesmo CPF, então o sistema deverá rejeitar com a mensagem "BENEFICIARIO JA CADASTRADO".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L137-L147`
- **Source Rule:** CADBENEF #8 (Confirmada parcial — RN-002)
- **Notas (discrepância a ratificar):** RN-002 bloqueia o CPF **apenas em situação ATIVA** (`STATUS='A'`) e permite reinclusão de excluído; o legado bloqueia **qualquer** CPF existente sem checar o status. O requisito reflete o comportamento confirmado no código. A decisão "bloquear sempre vs apenas ativo" é encaminhada para clarificação (`/speckit.clarify`).
- **Critérios de Aceite:**
  - [ ] Given um CPF já existente, when uma inclusão com esse CPF é submetida, then o sistema rejeita com "BENEFICIARIO JA CADASTRADO".
  - [ ] Given um CPF inexistente, when uma inclusão é submetida, then a unicidade não bloqueia o cadastro.

### REQ-008: Domínio fechado de status do beneficiário

- **EARS Pattern:** Unwanted behavior
- **Declaração:** Se o status informado para um beneficiário não for um de `A`, `S`, `C`, `I` ou `D`, então o sistema deverá rejeitar com a mensagem "STATUS INVALIDO".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN#L176-L182`
- **Source Rule:** VALBENEF #7 (Confirmada — define o domínio); rótulos confirmados em CONSBENF #4 e VALELEG #5
- **Notas:** Domínio resolvido por evidência (MYS-004 ✅): `A`=ativo, `S`=suspenso, `C`=cancelado, `I`=inativo, `D`=desligado.
- **Critérios de Aceite:**
  - [ ] Given status = `X`, when validado, then o sistema rejeita com "STATUS INVALIDO".
  - [ ] Given status ∈ {A,S,C,I,D}, when validado, then o status é aceito.

### REQ-009: Dependente exige titular existente

- **EARS Pattern:** Unwanted behavior
- **Declaração:** Se o CPF do titular informado não existir no cadastro de beneficiários, então o sistema deverá rejeitar a inclusão do dependente com a mensagem "BENEFICIARIO NAO ENCONTRADO".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CADDEPEND.NSN#L50-L53`
- **Source Rule:** CADDEPEND #1 (Confirmada — Manual 3.2.2)
- **Critérios de Aceite:**
  - [ ] Given CPF de titular inexistente, when um dependente é incluído, then o sistema rejeita com "BENEFICIARIO NAO ENCONTRADO".
  - [ ] Given CPF de titular existente, when um dependente é incluído, then a verificação de titular não bloqueia.

### REQ-010: Limite de dependentes por titular

- **EARS Pattern:** State-driven
- **Declaração:** Enquanto a quantidade de dependentes de um titular for maior que o limite configurado, o sistema deverá impedir novas inclusões com a mensagem "LIMITE DE DEPENDENTES ATINGIDO".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CADDEPEND.NSN#L62-L65`
- **Source Rule:** CADDEPEND #3 (Confirmada parcial)
- **Notas (discrepância a ratificar):** o legado usa limite **5** hardcoded; RN-004 e Manual 3.2.2 declaram **3**. O sistema modernizado deve **parametrizar** o limite; o valor oficial (3 ou 5) é decisão da equipe/facilitador.
- **Critérios de Aceite:**
  - [ ] Given um titular no limite de dependentes, when uma nova inclusão é tentada, then o sistema rejeita com "LIMITE DE DEPENDENTES ATINGIDO".
  - [ ] Given um titular abaixo do limite, when uma inclusão é tentada, then o dependente é aceito.

### REQ-011: Domínio de parentesco do dependente

- **EARS Pattern:** Unwanted behavior
- **Declaração:** Se o parentesco informado para um dependente não for um de `FI`, `CO`, `IR` ou `OU`, então o sistema deverá rejeitar com a mensagem "PARENTESCO INVALIDO".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CADDEPEND.NSN#L83-L87`
- **Source Rule:** CADDEPEND #5 (Confirmada parcial)
- **Notas:** o código aceita 4 valores (inclui `IR`=irmão); o Manual 3.2.2 cita apenas 3 (cônjuge, filho, outro). Confirmar o domínio oficial.
- **Critérios de Aceite:**
  - [ ] Given parentesco = `XX`, when validado, then o sistema rejeita com "PARENTESCO INVALIDO".
  - [ ] Given parentesco ∈ {FI,CO,IR,OU}, when validado, then o parentesco é aceito.

---

## Bounded Context: Programas Sociais e Elegibilidade

> Possui `PROGRAMA-SOCIAL`. Programas-fonte: CADPROG, VALELEG.

### REQ-012: Status ativo ao incluir programa

- **EARS Pattern:** Event-driven
- **Declaração:** Quando um programa social for incluído, o sistema deverá atribuir o status `A` (ativo) ao programa.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CADPROG.NSN#L97`
- **Source Rule:** CADPROG #5 (Confirmada — RN-003)
- **Critérios de Aceite:**
  - [ ] Given a inclusão de um programa, when persistido, then o status do programa é `A`.
  - [ ] Given uma consulta ao programa recém-incluído, when o status é lido, then retorna `A`.

### REQ-013: Elegibilidade exige programa ativo

- **EARS Pattern:** Unwanted behavior
- **Declaração:** Se o programa não estiver com status `A`, então o sistema deverá rejeitar a verificação de elegibilidade com a mensagem "PROGRAMA INATIVO".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L99-L102`
- **Source Rule:** VALELEG #3 (Confirmada — RN-003)
- **Critérios de Aceite:**
  - [ ] Given um programa com status ≠ `A`, when a elegibilidade é avaliada, then o sistema rejeita com "PROGRAMA INATIVO".
  - [ ] Given um programa com status `A`, when a elegibilidade é avaliada, then a verificação prossegue.

### REQ-014: Inelegibilidade por status do beneficiário

- **EARS Pattern:** State-driven
- **Declaração:** Enquanto o status do beneficiário não for `A`, o sistema deverá marcá-lo como inelegível com o motivo correspondente: `S`→"SUSPENSO", `C`/`D`→"CANCELADO/DESLIGADO", `I`→"INATIVO".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L116-L134`
- **Source Rule:** VALELEG #5 (Confirmada parcial — resolve domínio de status)
- **Critérios de Aceite:**
  - [ ] Given beneficiário com status `S`, when a elegibilidade é avaliada, then o resultado é inelegível com motivo "SUSPENSO".
  - [ ] Given beneficiário com status `A`, when a elegibilidade é avaliada, then a regra de status não o torna inelegível.

### REQ-015: Faixa etária definida pelo programa

- **EARS Pattern:** State-driven
- **Declaração:** Enquanto o programa definir idade mínima maior que zero, o sistema deverá marcar inelegível o beneficiário com idade inferior; enquanto definir idade máxima maior que zero, deverá marcar inelegível o beneficiário com idade superior.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L139-L152`
- **Source Rule:** VALELEG #6 (Confirmada)
- **Notas:** o cálculo de idade no legado usa apenas a diferença de anos (impreciso); o sistema modernizado deve calcular a idade com mês e dia.
- **Critérios de Aceite:**
  - [ ] Given programa com idade mínima 18 e beneficiário de 17 anos, when avaliado, then o resultado é inelegível.
  - [ ] Given programa com idade máxima 65 e beneficiário de 70 anos, when avaliado, then o resultado é inelegível.

### REQ-016: Teto de renda definido pelo programa

- **EARS Pattern:** State-driven
- **Declaração:** Enquanto o programa definir renda máxima maior que zero, o sistema deverá marcar inelegível o beneficiário cuja renda familiar exceda esse teto.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L157-L163`
- **Source Rule:** VALELEG #7 (Confirmada — RN-018)
- **Critérios de Aceite:**
  - [ ] Given programa com renda máxima 1000 e beneficiário com renda 1200, when avaliado, then o resultado é inelegível.
  - [ ] Given programa com renda máxima 1000 e beneficiário com renda 800, when avaliado, then a regra de renda não o torna inelegível.

### REQ-017: Regras de elegibilidade por tipo de programa

- **EARS Pattern:** Complex (Event-driven + condição)
- **Declaração:** Quando a elegibilidade for avaliada, o sistema deverá aplicar as regras por tipo de programa: para tipo `P` (previdenciário), exigir idade ≥ 60; para tipo `T` (trabalho), exigir idade entre 16 e 65; para tipo desconhecido, marcar inelegível.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L168-L201`
- **Source Rule:** VALELEG #8 (Confirmada parcial)
- **Notas:** a regra do tipo `A` (assistencial) no legado combina teto de renda 600 + documentação completa com magic numbers não documentados; essa parte fica em Open Questions (OQ relacionada a magic numbers de elegibilidade) até confirmação. As faixas etárias (60; 16–65) também carecem de doc formal.
- **Critérios de Aceite:**
  - [ ] Given programa tipo `P` e beneficiário de 58 anos, when avaliado, then o resultado é inelegível.
  - [ ] Given programa tipo `T` e beneficiário de 70 anos, when avaliado, then o resultado é inelegível.
  - [ ] Given programa de tipo não reconhecido, when avaliado, then o resultado é inelegível.

---

## Bounded Context: Cálculo de Benefícios

> Motor sem estado. Possui os parâmetros de cálculo (hoje hardcoded). Programas-fonte: CALCBENF, CALCCORR, CALCDSCT.
> **Atenção:** a fórmula nuclear do benefício, o 13º e a fonte da verdade do desconto são **bloqueadores**
> (MYS-010, MYS-012, MYS-013) e **não** aparecem como requisitos — ver Open Questions.

### REQ-018: Cálculo restrito a beneficiário ativo

- **EARS Pattern:** Unwanted behavior
- **Declaração:** Se o beneficiário não estiver com status `A` (ativo), então o sistema deverá rejeitar o cálculo do benefício com a mensagem "BENEFICIARIO NAO ATIVO".
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L160-L163`
- **Source Rule:** CALCBENF #3 (Confirmada — RN-021, seção 5.1)
- **Critérios de Aceite:**
  - [ ] Given beneficiário com status `S`, when o cálculo é solicitado, then o sistema rejeita com "BENEFICIARIO NAO ATIVO".
  - [ ] Given beneficiário com status `A`, when o cálculo é solicitado, then o cálculo prossegue.

### REQ-019: Truncamento de valores monetários para 2 casas

- **EARS Pattern:** Ubiquitous
- **Declaração:** O sistema deverá truncar (não arredondar) todos os valores monetários para 2 casas decimais.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L229-L231`, `01-arqueologia/legado-sifap/natural-programs/CALCCORR.NSN#L152-L156`
- **Source Rule:** CALCBENF #11, CALCCORR #6 (Confirmadas — RN-014)
- **Notas:** o relatório gerencial **arredonda** (BATCHREL), divergindo deste truncamento — conflito de conciliação registrado em OQ (MYS-026). A regra de pagamento é truncar.
- **Critérios de Aceite:**
  - [ ] Given um valor 123,4567, when truncado, then o resultado é 123,45.
  - [ ] Given um valor 99,999, when truncado, then o resultado é 99,99 (sem arredondamento para 100,00).

### REQ-020: Correção monetária por IPCA acumulado

- **EARS Pattern:** Ubiquitous
- **Declaração:** O sistema deverá corrigir o valor bruto de um pagamento acumulando o índice IPCA mês a mês do período, aplicando valor corrigido = valor original × índice acumulado.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CALCCORR.NSN#L150-L156`
- **Source Rule:** CALCCORR #4 (Confirmada parcial — RN-019)
- **Notas:** a tabela IPCA do legado cobre apenas 2010–2012; o comportamento para anos ausentes (índice 1,0 silencioso) é bloqueio operacional registrado em OQ (MYS-014). O requisito cobre a mecânica de acumulação; a cobertura/fonte da tabela IPCA fica pendente.
- **Critérios de Aceite:**
  - [ ] Given valor original 100,00 e índice acumulado 1,10 no período, when corrigido, then o valor corrigido é 110,00.
  - [ ] Given um período de múltiplos meses, when corrigido, then o índice aplicado é o produto dos índices mensais do período.

### REQ-021: Teto de desconto de 30% do valor bruto

- **EARS Pattern:** Ubiquitous
- **Declaração:** O sistema deverá definir um teto de desconto igual a 30% do valor bruto do pagamento.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L103-L107`
- **Source Rule:** CALCDSCT #4 (Confirmada — BR-013)
- **Critérios de Aceite:**
  - [ ] Given valor bruto 1000,00, when o teto é calculado, then o teto de desconto é 300,00.
  - [ ] Given o teto calculado, when truncado, then é expresso com 2 casas decimais.

### REQ-022: Desconto judicial sem teto

- **EARS Pattern:** Event-driven
- **Declaração:** Quando um desconto for do tipo `J` (judicial), o sistema deverá usar valor fixo (se informado) ou percentual sobre o bruto e somá-lo ao total **sem** aplicar o teto de 30%.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L127-L136`
- **Source Rule:** CALCDSCT #6 (Confirmada — BR-013, exceção legal)
- **Notas:** a pensão alimentícia (`P`) **não** é exceção ao teto no legado — questão jurídica em aberto (MYS-016, OQ-005). Apenas `J` é exceção confirmada.
- **Critérios de Aceite:**
  - [ ] Given um desconto judicial que excederia o teto, when aplicado, then o desconto é somado integralmente, sem limitação a 30%.
  - [ ] Given um desconto judicial com valor fixo informado, when aplicado, then o valor fixo prevalece sobre o percentual.

### REQ-023: Limitação dos descontos não judiciais a 30%

- **EARS Pattern:** State-driven
- **Declaração:** Enquanto o tipo de desconto não for `J`, o sistema deverá limitar o total acumulado de descontos a 30% do valor bruto.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L166-L171`
- **Source Rule:** CALCDSCT #12 (Confirmada — BR-013)
- **Notas:** o legado aplica o teto **por iteração** do grupo periódico, de modo que a ordem dos descontos afeta o resultado (efeito de ordem). O sistema modernizado deve definir uma ordem determinística — encaminhado para clarificação.
- **Critérios de Aceite:**
  - [ ] Given descontos não judiciais somando 40% do bruto, when limitados, then o total aplicado é 30% do bruto.
  - [ ] Given descontos não judiciais somando 20% do bruto, when limitados, then o total aplicado permanece 20%.

### REQ-024: Abono natalino para programa assistencial

- **EARS Pattern:** Optional feature
- **Declaração:** Onde a competência for dezembro e o programa for do tipo `A` (assistencial), o sistema deverá somar ao valor bruto um abono natalino de 15% do benefício mensal; caso contrário, o abono é zero.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L249-L258`
- **Source Rule:** CALCBENF #13 (Confirmada parcial — alteração 2009 "abono natalino")
- **Notas:** o percentual de 15% e a restrição ao tipo `A` são magic numbers sem doc formal; confirmar com o facilitador. A fórmula do **13º** (distinta do abono) é bloqueador (MYS-012) e não vira requisito.
- **Critérios de Aceite:**
  - [ ] Given competência de dezembro e programa tipo `A` com benefício mensal 100,00, when o abono é calculado, then o abono é 15,00.
  - [ ] Given competência de dezembro e programa tipo `P`, when o abono é calculado, then o abono é 0,00.

---

## Bounded Context: Processamento de Pagamentos

> Possui `PAGAMENTO`. Programas-fonte: BATCHPGT, BATCHCON. Consome o motor de Cálculo in-process.

### REQ-025: Processamento ordenado por CPF

- **EARS Pattern:** Ubiquitous
- **Declaração:** O sistema deverá processar os beneficiários do ciclo mensal em ordem crescente de CPF, preservando essa ordenação para sistemas a jusante que dela dependem.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L196-L201`
- **Source Rule:** BATCHPGT #1 (Confirmada — seção 5.1; comentário "SISTEMAS DOWNSTREAM DEPENDEM DESTA ORDENACAO")
- **Notas:** restrição de integração a preservar na migração.
- **Critérios de Aceite:**
  - [ ] Given um conjunto de beneficiários, when o ciclo mensal processa, then a saída está ordenada de forma crescente por CPF.
  - [ ] Given a ordenação por CPF, when verificada contra o consumidor a jusante, then a sequência é estável e reproduzível.

### REQ-026: Pagamento apenas para beneficiário ativo

- **EARS Pattern:** State-driven
- **Declaração:** Enquanto um beneficiário não estiver com status `A` (ativo), o sistema deverá ignorá-lo no ciclo de pagamento mensal.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L213-L216`
- **Source Rule:** BATCHPGT #3 (Confirmada — RN-021, seção 5.1)
- **Critérios de Aceite:**
  - [ ] Given um beneficiário com status `I`, when o ciclo mensal executa, then nenhum pagamento é gerado para ele.
  - [ ] Given um beneficiário com status `A`, when o ciclo mensal executa, then ele é considerado para geração de pagamento.

### REQ-027: Atualização de status por código de retorno bancário

- **EARS Pattern:** Event-driven
- **Declaração:** Quando um pagamento for conciliado, o sistema deverá atualizar o status conforme o código de retorno bancário: `00`→`P` (pago, gravando data e banco), `01`→`D` (devolvido), `02`→`E` (estornado).
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L180-L213`
- **Source Rule:** BATCHCON #4 (Confirmada — define status de pagamento P/D/E)
- **Notas:** código de retorno desconhecido deve ser registrado em log; o legado grava COD-BANCO fixo `1` (a generalizar).
- **Critérios de Aceite:**
  - [ ] Given retorno `00`, when conciliado, then o status do pagamento passa a `P` com data e banco gravados.
  - [ ] Given retorno `01`, when conciliado, then o status do pagamento passa a `D`.
  - [ ] Given retorno `02`, when conciliado, then o status do pagamento passa a `E`.

### REQ-028: Detecção de divergência de conciliação

- **EARS Pattern:** Unwanted behavior
- **Declaração:** Se a diferença absoluta entre o valor líquido do SIFAP e o valor retornado pelo banco for maior que R$ 0,01, então o sistema deverá marcar o pagamento como divergente e gravar um registro de auditoria.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L163-L178`
- **Source Rule:** BATCHCON #3 (Confirmada — auditoria, alteração 2014)
- **Critérios de Aceite:**
  - [ ] Given diferença de R$ 0,05 entre SIFAP e banco, when conciliado, then o pagamento é marcado divergente e um registro de auditoria é gravado.
  - [ ] Given diferença de R$ 0,01 ou menos, when conciliado, then nenhuma divergência é registrada.

### REQ-029: Registro de auditoria na conciliação

- **EARS Pattern:** Event-driven
- **Declaração:** Quando um pagamento for conciliado ou divergente, o sistema deverá gravar um registro de auditoria (ação `CO` para conciliação, `DV` para divergência) através da interface do contexto de Relatórios e Auditoria.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L215-L223`
- **Source Rule:** BATCHCON #5 (Confirmada)
- **Notas:** no Modular Monolith, a gravação ocorre via `AuditLog.record(...)` do contexto Relatórios e Auditoria — Pagamentos não escreve `AUDITORIA` diretamente (ver bounded-contexts.md).
- **Critérios de Aceite:**
  - [ ] Given um pagamento conciliado, when registrado, then existe um evento de auditoria com ação `CO`.
  - [ ] Given uma divergência detectada, when registrada, then existe um evento de auditoria com ação `DV`.

---

## Bounded Context: Relatórios e Auditoria

> Possui `AUDITORIA`. Programas-fonte: CONSBENF, RELPGT, BATCHREL, RELAUDIT.

### REQ-030: Domínio e rótulos de status de pagamento

- **EARS Pattern:** Ubiquitous
- **Declaração:** O sistema deverá reconhecer o domínio de status de pagamento `G` (gerado), `P` (pago), `C` (cancelado), `D` (devolvido) e `E` (estornado) e exibi-los com os rótulos correspondentes.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/BATCHREL.NSN#L176-L195`, `01-arqueologia/legado-sifap/natural-programs/RELPGT.NSN#L143-L156`
- **Source Rule:** BATCHREL #3, RELPGT #6 (Confirmadas — definem o domínio)
- **Critérios de Aceite:**
  - [ ] Given status de pagamento `P`, when exibido, then o rótulo é "PAGO".
  - [ ] Given status de pagamento fora do domínio, when exibido, then o sistema sinaliza valor desconhecido em vez de silenciá-lo como "GERADO".

### REQ-031: Rótulos de status do beneficiário na consulta

- **EARS Pattern:** Ubiquitous
- **Declaração:** O sistema deverá traduzir o status do beneficiário para exibição: `A`→ATIVO, `S`→SUSPENSO, `C`→CANCELADO, `I`→INATIVO, `D`→DESLIGADO.
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/CONSBENF.NSN#L121-L137`
- **Source Rule:** CONSBENF #4 (Confirmada — alinha com VALELEG/VALBENEF)
- **Critérios de Aceite:**
  - [ ] Given status `S`, when consultado, then o rótulo exibido é "SUSPENSO".
  - [ ] Given status `D`, when consultado, then o rótulo exibido é "DESLIGADO".

### REQ-032: Domínio de ações da trilha de auditoria

- **EARS Pattern:** Ubiquitous
- **Declaração:** O sistema deverá classificar cada evento de auditoria por ação: `IN` (inclusão), `AL` (alteração), `CO` (conciliação), `CN` (consulta) e `DV` (divergência).
- **source_legacy:** `01-arqueologia/legado-sifap/natural-programs/RELAUDIT.NSN#L151-L170`
- **Source Rule:** RELAUDIT #5 (Confirmada — define o domínio de ações)
- **Notas:** a ação `EX` (exclusão) é ocultada da trilha no legado — bloqueador de compliance (MYS-029, OQ-009). A trilha modernizada **deve** exibir exclusões; isso é tratado em Open Questions, não aqui.
- **Critérios de Aceite:**
  - [ ] Given um evento com ação `IN`, when classificado, then é contabilizado como "Inclusão".
  - [ ] Given um evento com ação `DV`, when classificado, then é contabilizado como "Divergência".

---

## Requisitos Greenfield

### REQ-033: Exposição via REST API versionada

- **EARS Pattern:** Ubiquitous
- **Declaração:** O sistema deverá expor as capacidades de cadastro, elegibilidade, cálculo, pagamento e consulta através de uma REST API sob o prefixo `/api/v1`, com verbos HTTP e status codes apropriados.
- **source_legacy:** `[GREENFIELD]` — mandato do workshop (stack-alvo Java 21 + Spring Boot REST; o legado é batch/3270 sem API). Justificativa: o sistema modernizado substitui telas 3270 e jobs batch por serviços acessíveis via API; não há equivalente no legado.
- **Source Rule:** N/A (nova capacidade)
- **Critérios de Aceite:**
  - [ ] Given um endpoint de recurso, when invocado, then o path segue `/api/v1/{resource}` e retorna o status code adequado (`201` criação, `204` sem conteúdo, `409` conflito).
  - [ ] Given a API, when documentada, then todos os endpoints possuem annotations OpenAPI/Swagger.

---

## Open Questions (Não São Requisitos Ainda)

> Mistérios `blocks-stage-2` de [`mysteries-found.md`](../01-arqueologia/mysteries-found.md). **Nenhum** vira
> requisito até ser resolvido. Cada item registra a informação necessária para destravar.

| OQ | Mistério | Pergunta a resolver | Informação necessária | Contexto afetado |
| --- | --- | --- | --- | --- |
| OQ-001 | MYS-001 — Fator-K `0.347215` | Por que a inclusão grava `VLR-BASE × (1 + FATOR-REAJUSTE × 0.347215)` em vez do valor informado? | Especificação oficial do Fator-K (REGRAS-NEGOCIO-2012 §6) ou decisão de eliminá-lo. | Programas Sociais e Elegibilidade / Cálculo |
| OQ-002 | MYS-010 — Fórmula multiplicativa vs aditiva | A fórmula do benefício é multiplicativa (5 fatores) ou aditiva (RN-013)? | Validação com folha de pagamento real + decisão de domínio. **Bloqueia toda EARS de cálculo nuclear.** | Cálculo de Benefícios |
| OQ-003 | MYS-012 — Fórmula do 13º (comentário ≠ código) | O 13º é proporcional a meses ativos ou usa fator idade? | Confirmação do facilitador sobre a fórmula correta do 13º. | Cálculo de Benefícios |
| OQ-004 | MYS-013 — Fonte da verdade do desconto | Qual dos 3 cálculos de desconto (3% fixo CALCBENF/BATCHPGT vs progressivo 3/5/7/9% CALCDSCT) é oficial? | Decisão de domínio + verificação se há desconto a menor no batch. | Cálculo de Benefícios |
| OQ-005 | MYS-016 — Pensão (`P`) sob teto de 30% | Pensão alimentícia deve ser exceção ao teto, como o desconto judicial? | Parecer jurídico/de domínio. | Cálculo de Benefícios |
| OQ-006 | MYS-017 — Bypass da região 99 | Replicar (com controle de acesso) ou eliminar a elegibilidade automática de COD-REGIAO=99? | Decisão de segurança/produto. | Programas Sociais e Elegibilidade |
| OQ-007 | MYS-019 — Backdoor de CPF `000` | Remover a aceitação de CPFs de teste (`000` + dígitos iguais) em produção? | Decisão de segurança. | Cadastro de Beneficiários |
| OQ-008 | MYS-021 — Backdoor de prefixos especiais (VALDOCS) | Eliminar o bypass que anula a validação documental para prefixos {000,001,002,010,011,099,100,999}? | Decisão de segurança + investigar uso de 099/100/999. | Cadastro de Beneficiários |
| OQ-009 | MYS-029 — Exclusões ocultas da trilha | A trilha modernizada deve exibir eventos `EX` (exclusão)? Eles ao menos são gravados hoje? | Decisão de compliance + verificação de gravação de `EX`. | Relatórios e Auditoria |

> **Open Questions de severidade menor** relevantes para os requisitos acima (não bloqueadoras, mas a
> ratificar antes da implementação): MYS-014 (cobertura da tabela IPCA — afeta REQ-020), MYS-026
> (relatório arredonda vs pagamento trunca — afeta REQ-019), limite de dependentes 5 vs 3 (afeta REQ-010),
> domínio de parentesco (afeta REQ-011), efeito de ordem no teto de desconto (afeta REQ-023), magic
> numbers de elegibilidade tipo `A` (afeta REQ-017).

---

## Matriz de Rastreabilidade

| REQ-ID | EARS Pattern | source_legacy | Source Rule # | Source File | Bounded Context |
| --- | --- | --- | --- | --- | --- |
| REQ-001 | Unwanted | CADBENEF.NSN#L105-L109 | CADBENEF #2 | CADBENEF.NSN | Cadastro de Beneficiários |
| REQ-002 | Ubiquitous | CADBENEF.NSN#L224-L269 | CADBENEF #4 | CADBENEF.NSN | Cadastro de Beneficiários |
| REQ-003 | Unwanted | CADBENEF.NSN#L111-L117; VALBENEF.NSN#L119-L124; VALDOCS.NSN#L74-L79 | CADBENEF #3 / VALBENEF #1 / VALDOCS #1 | CADBENEF.NSN, VALBENEF.NSN, VALDOCS.NSN | Cadastro de Beneficiários |
| REQ-004 | Unwanted | CADBENEF.NSN#L125-L129 | CADBENEF #6 | CADBENEF.NSN | Cadastro de Beneficiários |
| REQ-005 | Unwanted | VALBENEF.NSN#L143-L148 | VALBENEF #5 | VALBENEF.NSN | Cadastro de Beneficiários |
| REQ-006 | Event-driven | CADBENEF.NSN#L161-L164 | CADBENEF #10 | CADBENEF.NSN | Cadastro de Beneficiários |
| REQ-007 | Unwanted | CADBENEF.NSN#L137-L147 | CADBENEF #8 | CADBENEF.NSN | Cadastro de Beneficiários |
| REQ-008 | Unwanted | VALBENEF.NSN#L176-L182 | VALBENEF #7 | VALBENEF.NSN | Cadastro de Beneficiários |
| REQ-009 | Unwanted | CADDEPEND.NSN#L50-L53 | CADDEPEND #1 | CADDEPEND.NSN | Cadastro de Beneficiários |
| REQ-010 | State-driven | CADDEPEND.NSN#L62-L65 | CADDEPEND #3 | CADDEPEND.NSN | Cadastro de Beneficiários |
| REQ-011 | Unwanted | CADDEPEND.NSN#L83-L87 | CADDEPEND #5 | CADDEPEND.NSN | Cadastro de Beneficiários |
| REQ-012 | Event-driven | CADPROG.NSN#L97 | CADPROG #5 | CADPROG.NSN | Programas Sociais e Elegibilidade |
| REQ-013 | Unwanted | VALELEG.NSN#L99-L102 | VALELEG #3 | VALELEG.NSN | Programas Sociais e Elegibilidade |
| REQ-014 | State-driven | VALELEG.NSN#L116-L134 | VALELEG #5 | VALELEG.NSN | Programas Sociais e Elegibilidade |
| REQ-015 | State-driven | VALELEG.NSN#L139-L152 | VALELEG #6 | VALELEG.NSN | Programas Sociais e Elegibilidade |
| REQ-016 | State-driven | VALELEG.NSN#L157-L163 | VALELEG #7 | VALELEG.NSN | Programas Sociais e Elegibilidade |
| REQ-017 | Complex | VALELEG.NSN#L168-L201 | VALELEG #8 | VALELEG.NSN | Programas Sociais e Elegibilidade |
| REQ-018 | Unwanted | CALCBENF.NSN#L160-L163 | CALCBENF #3 | CALCBENF.NSN | Cálculo de Benefícios |
| REQ-019 | Ubiquitous | CALCBENF.NSN#L229-L231; CALCCORR.NSN#L152-L156 | CALCBENF #11 / CALCCORR #6 | CALCBENF.NSN, CALCCORR.NSN | Cálculo de Benefícios |
| REQ-020 | Ubiquitous | CALCCORR.NSN#L150-L156 | CALCCORR #4 | CALCCORR.NSN | Cálculo de Benefícios |
| REQ-021 | Ubiquitous | CALCDSCT.NSN#L103-L107 | CALCDSCT #4 | CALCDSCT.NSN | Cálculo de Benefícios |
| REQ-022 | Event-driven | CALCDSCT.NSN#L127-L136 | CALCDSCT #6 | CALCDSCT.NSN | Cálculo de Benefícios |
| REQ-023 | State-driven | CALCDSCT.NSN#L166-L171 | CALCDSCT #12 | CALCDSCT.NSN | Cálculo de Benefícios |
| REQ-024 | Optional | CALCBENF.NSN#L249-L258 | CALCBENF #13 | CALCBENF.NSN | Cálculo de Benefícios |
| REQ-025 | Ubiquitous | BATCHPGT.NSN#L196-L201 | BATCHPGT #1 | BATCHPGT.NSN | Processamento de Pagamentos |
| REQ-026 | State-driven | BATCHPGT.NSN#L213-L216 | BATCHPGT #3 | BATCHPGT.NSN | Processamento de Pagamentos |
| REQ-027 | Event-driven | BATCHCON.NSN#L180-L213 | BATCHCON #4 | BATCHCON.NSN | Processamento de Pagamentos |
| REQ-028 | Unwanted | BATCHCON.NSN#L163-L178 | BATCHCON #3 | BATCHCON.NSN | Processamento de Pagamentos |
| REQ-029 | Event-driven | BATCHCON.NSN#L215-L223 | BATCHCON #5 | BATCHCON.NSN | Processamento de Pagamentos |
| REQ-030 | Ubiquitous | BATCHREL.NSN#L176-L195; RELPGT.NSN#L143-L156 | BATCHREL #3 / RELPGT #6 | BATCHREL.NSN, RELPGT.NSN | Relatórios e Auditoria |
| REQ-031 | Ubiquitous | CONSBENF.NSN#L121-L137 | CONSBENF #4 | CONSBENF.NSN | Relatórios e Auditoria |
| REQ-032 | Ubiquitous | RELAUDIT.NSN#L151-L170 | RELAUDIT #5 | RELAUDIT.NSN | Relatórios e Auditoria |
| REQ-033 | Ubiquitous | [GREENFIELD] | N/A | — | Transversal (todos) |

---

## Resumo

- **Requisitos EARS:** 33 (REQ-001 a REQ-033), todos com `source_legacy` e ≥ 2 critérios de aceite.
- **Distribuição por contexto:** Cadastro de Beneficiários 11 · Programas Sociais e Elegibilidade 6 · Cálculo de Benefícios 7 · Processamento de Pagamentos 5 · Relatórios e Auditoria 3 · Greenfield 1.
- **Padrões EARS usados:** Ubiquitous, Event-driven, State-driven, Optional, Unwanted, Complex — todos os 6.
- **Open Questions:** 9 bloqueadores (`blocks-stage-2`) — nenhum promovido a requisito.
- **Fonte:** somente regras **Confirmadas**. Promoção de regras Inferidas requer decisão explícita da equipe.

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="bounded-contexts.md"><strong>bounded-contexts.md</strong></a><br/>
<sub>Os 5 contextos.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="GUIDE.md"><strong>GUIDE do Estágio 2</strong></a><br/>
<sub>ADRs e C4.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../README.md">Voltar ao Kit PT-BR</a></sub>
