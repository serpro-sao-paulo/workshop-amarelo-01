package br.gov.sifap.pagamento.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link TipoDesconto} as its legacy single-letter code. */
@Converter(autoApply = true)
public class TipoDescontoConverter implements AttributeConverter<TipoDesconto, String> {

    @Override
    public String convertToDatabaseColumn(TipoDesconto attribute) {
        return attribute == null ? null : attribute.codigo();
    }

    @Override
    public TipoDesconto convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TipoDesconto.fromCodigo(dbData);
    }
}
