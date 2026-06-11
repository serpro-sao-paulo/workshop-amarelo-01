import { afterEach, describe, expect, it, vi } from "vitest";

import { avaliarElegibilidade, listarProgramas } from "@/services/programas";

afterEach(() => {
  vi.restoreAllMocks();
});

describe("serviço de programas", () => {
  it("lista programas a partir da API", async () => {
    const page = {
      content: [
        {
          id: "1",
          codigo: "BF",
          nome: "Bolsa Família",
          tipo: "P",
          tipoRotulo: "Permanente",
          situacao: "A",
          situacaoRotulo: "Ativo",
          idadeMinima: 60,
          idadeMaxima: 0,
          rendaMaxima: 1000,
          vigenciaInicio: null,
          vigenciaFim: null,
        },
      ],
      page: 0,
      size: 20,
      totalElements: 1,
    };
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({
        ok: true,
        json: async () => page,
      }),
    );

    const resultado = await listarProgramas();

    expect(resultado.content).toHaveLength(1);
    expect(resultado.content[0]?.codigo).toBe("BF");
  });

  it("lança erro quando a API responde com falha", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({ ok: false, status: 500 }),
    );

    await expect(listarProgramas()).rejects.toThrow("Falha ao listar programas");
  });

  it("envia a avaliação de elegibilidade via POST", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ elegivel: false, motivos: ["RENDA ACIMA DO TETO"] }),
    });
    vi.stubGlobal("fetch", fetchMock);

    const resultado = await avaliarElegibilidade({
      beneficiarioId: "abc",
      programaCodigo: "BF",
      rendaFamiliar: 5000,
    });

    expect(resultado.elegivel).toBe(false);
    expect(resultado.motivos).toContain("RENDA ACIMA DO TETO");
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining("/api/v1/elegibilidade"),
      expect.objectContaining({ method: "POST" }),
    );
  });
});
