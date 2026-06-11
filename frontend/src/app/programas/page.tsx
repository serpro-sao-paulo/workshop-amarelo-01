import { ElegibilidadeForm } from "@/app/programas/elegibilidade-form";
import { listarProgramas } from "@/services/programas";

export async function ProgramasPage() {
  const pagina = await listarProgramas();

  return (
    <main className="mx-auto max-w-4xl p-8">
      <h1 className="mb-6 text-2xl font-semibold">Programas Sociais</h1>

      {pagina.content.length === 0 ? (
        <p className="text-gray-500">Nenhum programa cadastrado.</p>
      ) : (
        <table className="w-full border-collapse text-sm">
          <thead>
            <tr className="border-b text-left">
              <th className="py-2 pr-4">Código</th>
              <th className="py-2 pr-4">Nome</th>
              <th className="py-2 pr-4">Tipo</th>
              <th className="py-2 pr-4">Situação</th>
              <th className="py-2 pr-4">Idade mín.</th>
              <th className="py-2 pr-4">Idade máx.</th>
              <th className="py-2 pr-4">Renda máx.</th>
            </tr>
          </thead>
          <tbody>
            {pagina.content.map((programa) => (
              <tr key={programa.id} className="border-b">
                <td className="py-2 pr-4 font-mono">{programa.codigo}</td>
                <td className="py-2 pr-4">{programa.nome}</td>
                <td className="py-2 pr-4">{programa.tipoRotulo}</td>
                <td className="py-2 pr-4">{programa.situacaoRotulo}</td>
                <td className="py-2 pr-4">
                  {programa.idadeMinima > 0 ? programa.idadeMinima : "—"}
                </td>
                <td className="py-2 pr-4">
                  {programa.idadeMaxima > 0 ? programa.idadeMaxima : "—"}
                </td>
                <td className="py-2 pr-4">
                  {programa.rendaMaxima === null
                    ? "—"
                    : programa.rendaMaxima.toLocaleString("pt-BR", {
                        style: "currency",
                        currency: "BRL",
                      })}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <ElegibilidadeForm />
    </main>
  );
}

export default ProgramasPage;
