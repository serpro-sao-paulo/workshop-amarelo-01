package br.gov.sifap.shared.persistence;

import br.gov.sifap.shared.Money;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.math.BigDecimal;

/** Persists {@link Money} as a {@code numeric(15,2)} column. */
@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(Money attribute) {
        return attribute == null ? null : attribute.toBigDecimal();
    }

    @Override
    public Money convertToEntityAttribute(BigDecimal dbData) {
        return dbData == null ? null : Money.of(dbData);
    }
}
