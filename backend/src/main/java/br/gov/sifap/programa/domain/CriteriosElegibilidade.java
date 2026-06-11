package br.gov.sifap.programa.domain;

import br.gov.sifap.shared.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

/**
 * Eligibility criteria embedded in {@link ProgramaSocial} (REQ-015/016).
 *
 * <p>Age bounds equal to zero mean "no bound" ({@code VALELEG.NSN#L139-L152}).
 * {@code rendaMaxima} null/zero means "no income ceiling" (REQ-016).
 */
@Embeddable
public class CriteriosElegibilidade {

    @Column(name = "idade_minima", nullable = false)
    private int idadeMinima;

    @Column(name = "idade_maxima", nullable = false)
    private int idadeMaxima;

    @Convert(converter = br.gov.sifap.shared.persistence.MoneyConverter.class)
    @Column(name = "renda_maxima")
    private Money rendaMaxima;

    protected CriteriosElegibilidade() {
        // JPA
    }

    public CriteriosElegibilidade(int idadeMinima, int idadeMaxima, Money rendaMaxima) {
        this.idadeMinima = idadeMinima;
        this.idadeMaxima = idadeMaxima;
        this.rendaMaxima = rendaMaxima;
    }

    public int getIdadeMinima() {
        return idadeMinima;
    }

    public int getIdadeMaxima() {
        return idadeMaxima;
    }

    public Money getRendaMaxima() {
        return rendaMaxima;
    }

    /** REQ-015: program defines a minimum age greater than zero. */
    public boolean temIdadeMinima() {
        return idadeMinima > 0;
    }

    /** REQ-015: program defines a maximum age greater than zero. */
    public boolean temIdadeMaxima() {
        return idadeMaxima > 0;
    }

    /** REQ-016: program defines an income ceiling greater than zero. */
    public boolean temRendaMaxima() {
        return rendaMaxima != null && rendaMaxima.isGreaterThan(Money.ZERO);
    }
}
