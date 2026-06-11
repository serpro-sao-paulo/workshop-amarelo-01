package br.gov.sifap.beneficiario.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link SituacaoBeneficiario} as its legacy single-letter code. */
@Converter(autoApply = true)
public class SituacaoConverter implements AttributeConverter<SituacaoBeneficiario, String> {

    @Override
    public String convertToDatabaseColumn(SituacaoBeneficiario attribute) {
        return attribute == null ? null : attribute.codigo();
    }

    @Override
    public SituacaoBeneficiario convertToEntityAttribute(String dbData) {
        return dbData == null ? null : SituacaoBeneficiario.fromCodigo(dbData);
    }
}
