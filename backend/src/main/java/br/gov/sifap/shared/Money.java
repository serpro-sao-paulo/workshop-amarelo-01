package br.gov.sifap.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Monetary value object.
 *
 * <p>Values are always stored with scale 2 and <b>truncated</b> (never rounded),
 * reproducing the legacy behaviour confirmed in REQ-019
 * ({@code CALCBENF.NSN#L229-L231}, {@code CALCCORR.NSN#L152-L156}).
 */
public final class Money {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.DOWN);
    }

    public static Money of(BigDecimal amount) {
        return new Money(Objects.requireNonNull(amount, "amount"));
    }

    public static Money of(String amount) {
        return new Money(new BigDecimal(Objects.requireNonNull(amount, "amount")));
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    /** Percentage of this value, truncated to 2 decimals (e.g. {@code percentage(30)} = 30%). */
    public Money percentage(int percent) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN));
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    public BigDecimal toBigDecimal() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money other)) {
            return false;
        }
        return amount.compareTo(other.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
