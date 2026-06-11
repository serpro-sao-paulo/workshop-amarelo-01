import { listarTrilha } from "@/services/auditoria";

export async function RelatoriosPage() {
  const trilha = await listarTrilha();

  return (
    <main className="mx-auto max-w-5xl p-8">
      <h1 className="mb-6 text-2xl font-semibold">Trilha de Auditoria</h1>

      {trilha.length === 0 ? (
        <p className="text-gray-500">Nenhum evento registrado.</p>
      ) : (
        <table className="w-full border-collapse text-sm">
          <thead>
            <tr className="border-b text-left">
              <th className="py-2 pr-4">Momento</th>
              <th className="py-2 pr-4">Ação</th>
              <th className="py-2 pr-4">Entidade</th>
              <th className="py-2 pr-4">Detalhe</th>
            </tr>
          </thead>
          <tbody>
            {trilha.map((evento) => (
              <tr key={evento.id} className="border-b">
                <td className="py-2 pr-4 font-mono">
                  {new Date(evento.momento).toLocaleString("pt-BR")}
                </td>
                <td className="py-2 pr-4">{evento.acaoRotulo}</td>
                <td className="py-2 pr-4">{evento.entidade}</td>
                <td className="py-2 pr-4">{evento.detalhe ?? "—"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </main>
  );
}

export default RelatoriosPage;
