const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:8080";

export type Pagamento = {
  id: string;
  beneficiarioId: string;
  programaId: string;
  cpf: string;
  competencia: string;
  valorBase: number;
  valorCorrecao: number;
  valorLiquido: number;
  tipoPagamento: string;
  status: string;
  statusRotulo: string;
  divergente: boolean;
  dataGeracao: string;
  dataConciliacao: string | null;
};

export async function listarPagamentos(
  competencia: string,
): Promise<Pagamento[]> {
  const url = `${API_BASE_URL}/api/v1/pagamentos?competencia=${competencia}`;
  const response = await fetch(url, { cache: "no-store" });

  if (!response.ok) {
    throw new Error(`Falha ao listar pagamentos: ${response.status}`);
  }

  return (await response.json()) as Pagamento[];
}
