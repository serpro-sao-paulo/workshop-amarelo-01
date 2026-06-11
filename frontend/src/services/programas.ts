const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:8080";

export type TipoPrograma = "P" | "T" | "A";

export type Programa = {
  id: string;
  codigo: string;
  nome: string;
  tipo: TipoPrograma;
  tipoRotulo: string;
  situacao: string;
  situacaoRotulo: string;
  idadeMinima: number;
  idadeMaxima: number;
  rendaMaxima: number | null;
  vigenciaInicio: string | null;
  vigenciaFim: string | null;
};

export type ProgramaPage = {
  content: Programa[];
  page: number;
  size: number;
  totalElements: number;
};

export type ElegibilidadeResultado = {
  elegivel: boolean;
  motivos: string[];
};

export async function listarProgramas(
  page = 0,
  size = 20,
): Promise<ProgramaPage> {
  const url = `${API_BASE_URL}/api/v1/programas?page=${page}&size=${size}`;
  const response = await fetch(url, { cache: "no-store" });

  if (!response.ok) {
    throw new Error(`Falha ao listar programas: ${response.status}`);
  }

  return (await response.json()) as ProgramaPage;
}

export async function avaliarElegibilidade(input: {
  beneficiarioId: string;
  programaCodigo: string;
  rendaFamiliar: number | null;
}): Promise<ElegibilidadeResultado> {
  const url = `${API_BASE_URL}/api/v1/elegibilidade`;
  const response = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
    cache: "no-store",
  });

  if (!response.ok) {
    throw new Error(`Falha ao avaliar elegibilidade: ${response.status}`);
  }

  return (await response.json()) as ElegibilidadeResultado;
}
