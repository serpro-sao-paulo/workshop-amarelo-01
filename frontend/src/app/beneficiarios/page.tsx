import { listarBeneficiarios } from "@/services/beneficiarios";

const SITUACAO_ROTULO: Record<string, string> = {
  ATIVO: "Ativo",
  SUSPENSO: "Suspenso",
  CANCELADO: "Cancelado",
  INATIVO: "Inativo",
  DESLIGADO: "Desligado",
};

export async function BeneficiariosPage() {
  const pagina = await listarBeneficiarios();

  return (
    <main className="mx-auto max-w-4xl p-8">
      <h1 className="mb-6 text-2xl font-semibold">Beneficiários</h1>

      {pagina.content.length === 0 ? (
        <p className="text-gray-500">Nenhum beneficiário cadastrado.</p>
      ) : (
        <table className="w-full border-collapse text-sm">
          <thead>
            <tr className="border-b text-left">
              <th className="py-2 pr-4">Nome</th>
              <th className="py-2 pr-4">CPF</th>
              <th className="py-2 pr-4">Região</th>
              <th className="py-2 pr-4">Situação</th>
            </tr>
          </thead>
          <tbody>
            {pagina.content.map((beneficiario) => (
              <tr key={beneficiario.id} className="border-b">
                <td className="py-2 pr-4">{beneficiario.nome}</td>
                <td className="py-2 pr-4 font-mono">{beneficiario.cpf}</td>
                <td className="py-2 pr-4">{beneficiario.regiao}</td>
                <td className="py-2 pr-4">
                  {SITUACAO_ROTULO[beneficiario.situacao] ??
                    beneficiario.situacao}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </main>
  );
}

export default BeneficiariosPage;
