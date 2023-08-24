package com.lima.api.gerenciador.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
