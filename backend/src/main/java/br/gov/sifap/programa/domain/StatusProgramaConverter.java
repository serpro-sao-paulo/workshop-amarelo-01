package br.gov.sifap.programa.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link StatusPrograma} as its legacy single-letter code. */
@Converter(autoApply = true)
public class StatusProgramaConverter implements AttributeConverter<StatusPrograma, String> {

    @Override
    public String convertToDatabaseColumn(StatusPrograma attribute) {
        return attribute == null ? null : attribute.codigo();
    }

    @Override
    public StatusPrograma convertToEntityAttribute(String dbData) {
        return dbData == null ? null : StatusPrograma.fromCodigo(dbData);
    }
}
