package com.bank.cuenta.infrastructure.persistence.converter;

import com.bank.cuenta.domain.entity.TipoMovimiento;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TipoMovimientoConverter implements AttributeConverter<TipoMovimiento, String> {

    @Override
    public String convertToDatabaseColumn(TipoMovimiento attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TipoMovimiento convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TipoMovimiento.fromValue(dbData);
    }
}
