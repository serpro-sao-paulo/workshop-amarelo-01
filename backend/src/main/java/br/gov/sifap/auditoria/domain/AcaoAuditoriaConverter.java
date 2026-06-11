package br.gov.sifap.auditoria.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link AcaoAuditoria} as its legacy two-letter code. */
@Converter(autoApply = true)
public class AcaoAuditoriaConverter implements AttributeConverter<AcaoAuditoria, String> {

    @Override
    public String convertToDatabaseColumn(AcaoAuditoria attribute) {
        return attribute == null ? null : attribute.codigo();
    }

    @Override
    public AcaoAuditoria convertToEntityAttribute(String dbData) {
        return dbData == null ? null : AcaoAuditoria.fromCodigo(dbData);
    }
}
