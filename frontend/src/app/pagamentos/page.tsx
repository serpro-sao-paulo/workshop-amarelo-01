import { listarPagamentos } from "@/services/pagamentos";

const BRL = (valor: number): string =>
  valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });

type PagamentosPageProps = {
  searchParams: Promise<{ competencia?: string }>;
};

function competenciaAtual(): string {
  const agora = new Date();
  const mes = String(agora.getUTCMonth() + 1).padStart(2, "0");
  return `${agora.getUTCFullYear()}-${mes}`;
}

export async function PagamentosPage({ searchParams }: PagamentosPageProps) {
  const { competencia } = await searchParams;
  const referencia = competencia ?? competenciaAtual();
  const pagamentos = await listarPagamentos(referencia);

  return (
    <main className="mx-auto max-w-5xl p-8">
      <h1 className="mb-2 text-2xl font-semibold">Pagamentos</h1>
      <p className="mb-6 text-sm text-gray-500">Competência {referencia}</p>

      {pagamentos.length === 0 ? (
        <p className="text-gray-500">
          Nenhum pagamento para a competência informada.
        </p>
      ) : (
        <table className="w-full border-collapse text-sm">
          <thead>
            <tr className="border-b text-left">
              <th className="py-2 pr-4">CPF</th>
              <th className="py-2 pr-4">Valor base</th>
              <th className="py-2 pr-4">Correção</th>
              <th className="py-2 pr-4">Líquido</th>
              <th className="py-2 pr-4">Situação</th>
              <th className="py-2 pr-4">Divergente</th>
            </tr>
          </thead>
          <tbody>
            {pagamentos.map((pagamento) => (
              <tr key={pagamento.id} className="border-b">
                <td className="py-2 pr-4 font-mono">{pagamento.cpf}</td>
                <td className="py-2 pr-4">{BRL(pagamento.valorBase)}</td>
                <td className="py-2 pr-4">{BRL(pagamento.valorCorrecao)}</td>
                <td className="py-2 pr-4">{BRL(pagamento.valorLiquido)}</td>
                <td className="py-2 pr-4">{pagamento.statusRotulo}</td>
                <td className="py-2 pr-4">
                  {pagamento.divergente ? "Sim" : "Não"}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </main>
  );
}

export default PagamentosPage;
