package br.gov.sifap.programa.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link TipoPrograma} as its legacy single-letter code. */
@Converter(autoApply = true)
public class TipoProgramaConverter implements AttributeConverter<TipoPrograma, String> {

    @Override
    public String convertToDatabaseColumn(TipoPrograma attribute) {
        return attribute == null ? null : attribute.codigo();
    }

    @Override
    public TipoPrograma convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TipoPrograma.fromCodigo(dbData);
    }
}
