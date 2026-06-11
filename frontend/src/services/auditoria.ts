const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:8080";

export type EventoAuditoria = {
  id: string;
  acao: string;
  acaoRotulo: string;
  entidade: string;
  entidadeId: string | null;
  detalhe: string | null;
  momento: string;
};

export async function listarTrilha(): Promise<EventoAuditoria[]> {
  const url = `${API_BASE_URL}/api/v1/auditoria`;
  const response = await fetch(url, { cache: "no-store" });

  if (!response.ok) {
    throw new Error(`Falha ao listar trilha de auditoria: ${response.status}`);
  }

  return (await response.json()) as EventoAuditoria[];
}
