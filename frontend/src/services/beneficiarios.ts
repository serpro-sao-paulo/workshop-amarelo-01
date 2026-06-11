const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:8080";

export type SituacaoBeneficiario =
  | "ATIVO"
  | "SUSPENSO"
  | "CANCELADO"
  | "INATIVO"
  | "DESLIGADO";

export type Beneficiario = {
  id: string;
  cpf: string;
  nome: string;
  dataNascimento: string;
  regiao: string;
  situacao: SituacaoBeneficiario;
  dataCadastro: string;
};

export type BeneficiarioPage = {
  content: Beneficiario[];
  page: number;
  size: number;
  totalElements: number;
};

export async function listarBeneficiarios(
  page = 0,
  size = 20,
): Promise<BeneficiarioPage> {
  const url = `${API_BASE_URL}/api/v1/beneficiarios?page=${page}&size=${size}`;
  const response = await fetch(url, { cache: "no-store" });

  if (!response.ok) {
    throw new Error(`Falha ao listar beneficiários: ${response.status}`);
  }

  return (await response.json()) as BeneficiarioPage;
}
