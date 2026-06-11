<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Inventário Legado — [Nome da Equipe]

> ⚠️ **Primeira passada (top-down).** Este inventário foi gerado por `/archaeology-kickoff`
> trabalhando **apenas sobre nomes de arquivos e estrutura de pastas** — nenhum programa
> `.NSN` ou DDM foi aberto/lido. As hipóteses serão revisadas conforme a equipe ler os
> arquivos individuais (`/extract-business-rules`, `/map-dependencies`).

**Data:** 2026-06-10
**Caminho escaneado:** `01-arqueologia/legado-sifap/`

---

## Estrutura de Pastas

```
01-arqueologia/legado-sifap/
├── README.md
├── adabas-ddms/
│   ├── README.md
│   ├── AUDITORIA.ddm
│   ├── BENEFICIARIO.ddm
│   ├── PAGAMENTO.ddm
│   └── PROGRAMA-SOCIAL.ddm
├── legacy-docs/
│   ├── README.md
│   ├── ARQUITETURA-ORIGINAL-1997.md
│   ├── ARQUITETURA-ORIGINAL-1997.docx
│   ├── MANUAL-TECNICO-SIFAP-2008.md
│   ├── MANUAL-TECNICO-SIFAP-2008.docx
│   ├── REGRAS-NEGOCIO-2012.md
│   └── REGRAS-NEGOCIO-2012.docx
└── natural-programs/
    ├── README.md
    ├── BATCHCON.NSN
    ├── BATCHPGT.NSN
    ├── BATCHREL.NSN
    ├── CADBENEF.NSN
    ├── CADDEPEND.NSN
    ├── CADPROG.NSN
    ├── CALCBENF.NSN
    ├── CALCCORR.NSN
    ├── CALCDSCT.NSN
    ├── CONSBENF.NSN
    ├── RELAUDIT.NSN
    ├── RELPGT.NSN
    ├── VALBENEF.NSN
    ├── VALDOCS.NSN
    └── VALELEG.NSN
```

**Total de diretórios:** 4 (`legado-sifap/` raiz + 3 subdiretórios: `adabas-ddms/`, `legacy-docs/`, `natural-programs/`).

> Verificação sugerida (segunda pessoa da equipe):
> `find 01-arqueologia/legado-sifap -type d` deve retornar 4 linhas.

---

## Contagem de Arquivos por Tipo

| Extensão | Contagem | Finalidade provável                                          |
| -------- | -------- | ------------------------------------------------------------ |
| `.NSN`   | 15       | Programa-fonte Natural                                        |
| `.ddm`   | 4        | Data Definition Module (estrutura Adabas)                    |
| `.md`    | 7        | Markdown (4 docs/README em `legacy-docs/` + 3 READMEs de pasta) |
| `.docx`  | 3        | Documento Word (formato original dos docs legados)           |

> Verificação sugerida: `find 01-arqueologia/legado-sifap -name '*.NSN' | wc -l` → 15;
> `find 01-arqueologia/legado-sifap -name '*.ddm' | wc -l` → 4.
>
> Observação: **não foram encontrados** arquivos `.cpy` (copycode) nem `.map` (telas 3270),
> embora os READMEs/docs mencionem subprogramas (`VALCPF`, `VALNISN`), copycode (`FMTVLR`)
> e maps. Esses artefatos **não estão materializados** nesta pasta — ver "Itens Incomuns".

---

## Padrões de Convenção de Nomes

Agrupamento por prefixo dos nomes `.NSN` (somente nomes — arquivos não abertos):

| Prefixo  | Contagem | Hipótese                                                                 |
| -------- | -------- | ------------------------------------------------------------------------ |
| `BATCH-` | 3        | Programas batch / entry points (`BATCHCON`, `BATCHPGT`, `BATCHREL`)      |
| `CAD-`   | 3        | Cadastro / CRUD online (`CADBENEF`, `CADDEPEND`, `CADPROG`)              |
| `CALC-`  | 3        | Rotinas de cálculo (`CALCBENF`, `CALCCORR`, `CALCDSCT`)                  |
| `VAL-`   | 3        | Rotinas de validação (`VALBENEF`, `VALDOCS`, `VALELEG`)                  |
| `REL-`   | 2        | Geração de relatórios (`RELAUDIT`, `RELPGT`)                             |
| `CONS-`  | 1        | Consulta — prefixo de ocorrência única (não atinge 2+); ver Item Incomum |

> 5 padrões com 2+ arquivos identificados. As hipóteses baseiam-se apenas em convenções
> genéricas Natural — serão confirmadas ao ler o código.
>
> Os 4 DDMs **não seguem prefixo**: usam o nome da entidade
> (`AUDITORIA`, `BENEFICIARIO`, `PAGAMENTO`, `PROGRAMA-SOCIAL`).

---

## Itens Incomuns (Top 3)

| #   | Caminho do Arquivo                                       | O Que o Torna Incomum                                                                                                                            | Investigação Sugerida                                                                                                |
| --- | ------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------- |
| 1   | `legado-sifap/natural-programs/CONSBENF.NSN`            | Único programa com prefixo `CONS-` (ocorrência única) — todos os demais prefixos têm 2+ membros.                                                | Verificar se é o único ponto de consulta online e por que não forma família com outros (`/extract-business-rules`). |
| 2   | `legado-sifap/adabas-ddms/PROGRAMA-SOCIAL.ddm`          | Único DDM com nome composto/hifenizado — os outros 3 são nomes simples de uma palavra.                                                          | Ler o DDM com o Par 4 (DBA+QA): provável presença de campos `MU`/`PE` (faixas por exercício) → várias tabelas SQL.  |
| 3   | `legado-sifap/legacy-docs/*.docx` (3 arquivos)          | Única extensão **binária** do acervo; cada `.docx` é par de um `.md`. Risco de divergência de conteúdo entre as duas versões.                   | Confirmar se algum `.docx` contém conteúdo (anotações/revisões) ausente no `.md` convertido antes de confiar no md. |

> Nota adicional (não contabilizada no Top 3): **ausência** de arquivos `.cpy`/`.map` e de
> subprogramas citados na documentação (`VALCPF`, `VALNISN`, `FMTVLR`, integração CadÚnico).
> Tratar como lacuna conhecida ao montar o mapa de dependências.

---

## Ordem de Leitura Proposta

> ⚠️ **Hipótese.** A ordem real mudará quando a equipe começar a rastrear dependências
> (CALLNAT) e a relação programas ↔ DDMs.

1. **DDMs primeiro (dados antes do código):**
   `BENEFICIARIO.ddm` → `PROGRAMA-SOCIAL.ddm` → `PAGAMENTO.ddm` → `AUDITORIA.ddm`.
   Entender as entidades antes de ler qualquer lógica.
2. **Entry points batch (revelam o fluxo de negócio):**
   `BATCHPGT.NSN` (provável orquestrador da folha mensal) → `BATCHCON.NSN` → `BATCHREL.NSN`.
3. **Programas mais conectados (núcleo chamado pelos batches):**
   `CALCBENF.NSN`, `CALCCORR.NSN`, `CALCDSCT.NSN` e `VALELEG.NSN` — hipótese de serem
   invocados via CALLNAT pelo `BATCHPGT`. Confirmar no mapa de dependências.
4. **Cadastro e validação restantes:**
   `CADBENEF.NSN`, `CADDEPEND.NSN`, `CADPROG.NSN`, `VALBENEF.NSN`, `VALDOCS.NSN`.
5. **Consulta e relatórios (o que o usuário vê):**
   `CONSBENF.NSN`, `RELPGT.NSN`, `RELAUDIT.NSN`.

**Justificativa:** a ordem prioriza (a) DDMs para fixar o vocabulário de dados, (b) os
prefixos `BATCH-` como pontos de entrada e (c) os prefixos `CALC-`/`VAL-` como candidatos
a maior conectividade (subprogramas de regra). Nada disso foi confirmado por leitura de
conteúdo — é ponto de partida, não conclusão.

---

**Lembrete (Definição de Pronto):** inventário existe · contagens verificáveis por `find`
· 5 padrões de nomes identificados · 3 itens incomuns sinalizados com caminho e motivo ·
ordem de leitura justificada. **Revisar conforme a equipe ler os arquivos.**
