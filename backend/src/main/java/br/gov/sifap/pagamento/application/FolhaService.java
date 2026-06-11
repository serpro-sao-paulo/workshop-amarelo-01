package br.gov.sifap.pagamento.application;

import br.gov.sifap.beneficiario.application.BeneficiarioFolhaView;
import br.gov.sifap.beneficiario.application.BeneficiarioQuery;
import br.gov.sifap.calculo.application.DescontoResultado;
import br.gov.sifap.calculo.application.DescontoService;
import br.gov.sifap.calculo.domain.CalculoBeneficio;
import br.gov.sifap.calculo.domain.CalculoInput;
import br.gov.sifap.pagamento.domain.Desconto;
import br.gov.sifap.pagamento.domain.Pagamento;
import br.gov.sifap.pagamento.domain.TipoPagamento;
import br.gov.sifap.pagamento.infrastructure.PagamentoRepository;
import br.gov.sifap.shared.Money;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Monthly payroll service (REQ-018/025/026). The ONLY writer of the PAGAMENTO
 * aggregate (ADR-001); orchestrates the stateless Cálculo engine.
 *
 * <p>Beneficiaries are processed in ascending CPF order (REQ-025) and only when
 * active (REQ-026). The benefit value formula is DEFERRED (D-01): {@link #gerarFolha}
 * delegates to {@link CalculoBeneficio}, which throws until the formula is
 * ratified. {@link #selecionarParaFolha} exposes the confirmed selection/ordering
 * for independent testing.
 */
@Service
public class FolhaService {

    private final BeneficiarioQuery beneficiarios;
    private final PagamentoRepository pagamentos;
    private final CalculoBeneficio calculo;
    private final DescontoService descontos;

    public FolhaService(
            BeneficiarioQuery beneficiarios,
            PagamentoRepository pagamentos,
            CalculoBeneficio calculo,
            DescontoService descontos) {
        this.beneficiarios = beneficiarios;
        this.pagamentos = pagamentos;
        this.calculo = calculo;
        this.descontos = descontos;
    }

    /** REQ-025/026: active beneficiaries ordered ascending by CPF. */
    @Transactional(readOnly = true)
    public List<BeneficiarioFolhaView> selecionarParaFolha() {
        return beneficiarios.ativosOrdenadosPorCpf();
    }

    /**
     * Generates the monthly payroll. ⏸️ The value formula is DEFERRED (D-01): this
     * delegates to {@link CalculoBeneficio#calcularValorBase}, which throws until
     * the team ratifies the formula.
     */
    @Transactional
    public List<Pagamento> gerarFolha(YearMonth competencia, java.util.UUID programaId) {
        List<Pagamento> gerados = new ArrayList<>();
        for (BeneficiarioFolhaView beneficiario : selecionarParaFolha()) { // REQ-025/026
            Money valorBase = calculo.calcularValorBase(
                    new CalculoInput(beneficiario.id(), programaId, competencia)); // ⏸️ DEFERRED D-01

            List<Desconto> aplicaveis = List.of();
            DescontoResultado desconto = descontos.aplicar(valorBase, aplicaveis); // REQ-021/022/023

            Pagamento pagamento = Pagamento.gerar(
                    beneficiario.id(), programaId, beneficiario.cpf(), competencia,
                    valorBase, Money.ZERO, desconto.total(), TipoPagamento.MENSAL, aplicaveis);
            gerados.add(pagamentos.save(pagamento));
        }
        return gerados;
    }
}
