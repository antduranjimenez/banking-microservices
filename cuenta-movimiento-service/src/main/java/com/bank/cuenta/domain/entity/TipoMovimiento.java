package com.bank.cuenta.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum TipoMovimiento {
    DEPOSITO("Deposito"),
    RETIRO("Retiro");

    private final String value;

    TipoMovimiento(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TipoMovimiento fromValue(String value) {
        return Arrays.stream(values())
                .filter(t -> t.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de movimiento desconocido: " + value));
    }
}
