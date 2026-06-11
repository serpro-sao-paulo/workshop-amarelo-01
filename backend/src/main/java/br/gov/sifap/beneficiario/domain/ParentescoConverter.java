package br.gov.sifap.beneficiario.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link Parentesco} as its legacy two-letter code. */
@Converter(autoApply = true)
public class ParentescoConverter implements AttributeConverter<Parentesco, String> {

    @Override
    public String convertToDatabaseColumn(Parentesco attribute) {
        return attribute == null ? null : attribute.codigo();
    }

    @Override
    public Parentesco convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Parentesco.fromCodigo(dbData);
    }
}
