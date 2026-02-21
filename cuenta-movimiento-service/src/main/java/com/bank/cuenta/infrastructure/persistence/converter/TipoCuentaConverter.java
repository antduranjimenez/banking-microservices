package com.bank.cuenta.infrastructure.persistence.converter;

import com.bank.cuenta.domain.entity.TipoCuenta;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TipoCuentaConverter implements AttributeConverter<TipoCuenta, String> {

    @Override
    public String convertToDatabaseColumn(TipoCuenta attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TipoCuenta convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TipoCuenta.fromValue(dbData);
    }
}
