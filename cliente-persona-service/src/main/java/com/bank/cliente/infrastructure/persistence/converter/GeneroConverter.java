package com.bank.cliente.infrastructure.persistence.converter;

import com.bank.cliente.domain.entity.Genero;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GeneroConverter implements AttributeConverter<Genero, String> {

    @Override
    public String convertToDatabaseColumn(Genero attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Genero convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Genero.fromValue(dbData);
    }
}
