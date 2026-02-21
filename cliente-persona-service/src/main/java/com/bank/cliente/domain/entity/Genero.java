package com.bank.cliente.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Genero {
    MASCULINO("Masculino"),
    FEMENINO("Femenino");

    private final String value;

    Genero(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Genero fromValue(String value) {
        return Arrays.stream(values())
                .filter(g -> g.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Genero desconocido: " + value));
    }
}
