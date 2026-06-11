package br.gov.sifap.pagamento.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link StatusPagamento} as its legacy single-letter code. */
@Converter(autoApply = true)
public class StatusPagamentoConverter implements AttributeConverter<StatusPagamento, String> {

    @Override
    public String convertToDatabaseColumn(StatusPagamento attribute) {
        return attribute == null ? null : attribute.codigo();
    }

    @Override
    public StatusPagamento convertToEntityAttribute(String dbData) {
        return dbData == null ? null : StatusPagamento.fromCodigo(dbData);
    }
}
