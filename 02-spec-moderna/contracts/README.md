# Contratos de API — Modernização SIFAP

> Contrato REST `/api/v1` (REQ-033, `[GREENFIELD]`) sobre os 5 bounded contexts. Define **forma** dos endpoints — não implementação. Endpoints/campos cujo cálculo depende de Open Questions deferidas (D-01..D-03) retornam a estrutura, mas o **valor** só é confiável após resolução das fórmulas.

| Arquivo | Conteúdo |
| --- | --- |
| [openapi.yaml](openapi.yaml) | Especificação OpenAPI 3.1 dos recursos `/api/v1` |

## Mapa recurso → bounded context → requisitos

| Recurso | Bounded context | Requisitos |
| --- | --- | --- |
| `/api/v1/beneficiarios` | Cadastro de Beneficiários | REQ-001..011, REQ-033 |
| `/api/v1/programas` · `/api/v1/elegibilidade` | Programas Sociais e Elegibilidade | REQ-012..017 |
| `/api/v1/pagamentos` | Processamento de Pagamentos | REQ-025..029 |
| `/api/v1/relatorios` · `/api/v1/auditoria` · `/api/v1/consultas` | Relatórios e Auditoria | REQ-030..032 |

## Convenções (copilot-instructions)

- Path: `/api/v1/{resource}`; verbos HTTP corretos; status `201`/`204`/`409` apropriados.
- Todos os endpoints com annotations OpenAPI/Swagger (springdoc).
- Autenticação OAuth2/JWT (ADR-005); CORS explícito (sem `*` em prod).
- CPF **mascarado** em respostas de listagem/log; valores monetários truncados a 2 casas (REQ-019).

## Itens deferidos refletidos no contrato

- `POST /api/v1/pagamentos/folha` (gerar folha): cálculo de `valorBase`/13º/descontos por faixa **deferido** (D-01/02/03) — o endpoint existe, mas só deve ser ligado após confirmação das fórmulas.
- Exceções de acesso legadas (região 99, CPF 000, prefixos) **não** são expostas como parâmetro público; são regras internas auditadas (ADR-005, OQ-006/007/008).
