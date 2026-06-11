"use client";

import { useActionState } from "react";

import {
  avaliarElegibilidadeAction,
  type ElegibilidadeFormState,
} from "@/app/programas/actions";

const ESTADO_INICIAL: ElegibilidadeFormState = {
  elegivel: null,
  motivos: [],
  erro: null,
};

export function ElegibilidadeForm() {
  const [state, formAction, pending] = useActionState(
    avaliarElegibilidadeAction,
    ESTADO_INICIAL,
  );

  return (
    <section className="mt-10 rounded-lg border p-6">
      <h2 className="mb-4 text-lg font-semibold">Simulação de elegibilidade</h2>

      <form action={formAction} className="grid gap-4 sm:grid-cols-3">
        <label className="flex flex-col text-sm">
          Beneficiário (ID)
          <input
            name="beneficiarioId"
            className="mt-1 rounded border px-2 py-1"
            required
          />
        </label>
        <label className="flex flex-col text-sm">
          Código do programa
          <input
            name="programaCodigo"
            className="mt-1 rounded border px-2 py-1"
            required
          />
        </label>
        <label className="flex flex-col text-sm">
          Renda familiar
          <input
            name="rendaFamiliar"
            type="number"
            step="0.01"
            min="0"
            className="mt-1 rounded border px-2 py-1"
          />
        </label>

        <button
          type="submit"
          disabled={pending}
          className="rounded bg-blue-600 px-4 py-2 text-sm font-medium text-white disabled:opacity-50 sm:col-span-3 sm:w-fit"
        >
          {pending ? "Avaliando…" : "Avaliar"}
        </button>
      </form>

      {state.erro !== null && (
        <p className="mt-4 text-sm text-red-600">{state.erro}</p>
      )}

      {state.elegivel !== null && (
        <div className="mt-4 text-sm">
          <p className="font-medium">
            Resultado: {state.elegivel ? "Elegível" : "Inelegível"}
          </p>
          {state.motivos.length > 0 && (
            <ul className="mt-2 list-inside list-disc text-gray-600">
              {state.motivos.map((motivo) => (
                <li key={motivo}>{motivo}</li>
              ))}
            </ul>
          )}
        </div>
      )}
    </section>
  );
}
