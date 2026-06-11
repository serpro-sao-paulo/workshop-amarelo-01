package br.gov.sifap.pagamento.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link TipoPagamento} as its legacy single-letter code. */
@Converter(autoApply = true)
public class TipoPagamentoConverter implements AttributeConverter<TipoPagamento, String> {

    @Override
    public String convertToDatabaseColumn(TipoPagamento attribute) {
        return attribute == null ? null : attribute.codigo();
    }

    @Override
    public TipoPagamento convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TipoPagamento.fromCodigo(dbData);
    }
}
