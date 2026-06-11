package br.gov.sifap.shared.persistence;

import br.gov.sifap.shared.CPF;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link CPF} as its 11 unmasked digits. */
@Converter(autoApply = true)
public class CpfConverter implements AttributeConverter<CPF, String> {

    @Override
    public String convertToDatabaseColumn(CPF attribute) {
        return attribute == null ? null : attribute.unmasked();
    }

    @Override
    public CPF convertToEntityAttribute(String dbData) {
        return dbData == null ? null : CPF.of(dbData);
    }
}
