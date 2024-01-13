package com.lima.api.gerenciador.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn(Boolean value) {
		return value ? "sim" : "nao";
	}

	@Override
	public Boolean convertToEntityAttribute(String value) {
		return "sim".equalsIgnoreCase(value);
	}
}
