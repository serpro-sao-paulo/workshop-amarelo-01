"use server";

import { avaliarElegibilidade } from "@/services/programas";

export type ElegibilidadeFormState = {
  elegivel: boolean | null;
  motivos: string[];
  erro: string | null;
};

export async function avaliarElegibilidadeAction(
  _prev: ElegibilidadeFormState,
  formData: FormData,
): Promise<ElegibilidadeFormState> {
  const beneficiarioId = String(formData.get("beneficiarioId") ?? "").trim();
  const programaCodigo = String(formData.get("programaCodigo") ?? "").trim();
  const rendaRaw = String(formData.get("rendaFamiliar") ?? "").trim();

  if (beneficiarioId === "" || programaCodigo === "") {
    return {
      elegivel: null,
      motivos: [],
      erro: "Informe o beneficiário e o código do programa.",
    };
  }

  const rendaFamiliar = rendaRaw === "" ? null : Number(rendaRaw);
  if (rendaFamiliar !== null && Number.isNaN(rendaFamiliar)) {
    return { elegivel: null, motivos: [], erro: "Renda familiar inválida." };
  }

  try {
    const resultado = await avaliarElegibilidade({
      beneficiarioId,
      programaCodigo,
      rendaFamiliar,
    });
    return { elegivel: resultado.elegivel, motivos: resultado.motivos, erro: null };
  } catch {
    return {
      elegivel: null,
      motivos: [],
      erro: "Não foi possível avaliar a elegibilidade.",
    };
  }
}
