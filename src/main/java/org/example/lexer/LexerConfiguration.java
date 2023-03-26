package org.example.lexer;

import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LexerConfiguration {

	public static final int BASE_TEN = 10;
	public static final int MAX_IDENTIFIER_LENGTH = getProperty("max.identifier.length", 100);
	public static final int MAX_STRING_LENGTH = getProperty("max.string.length", 1_000);

	private static int getProperty(String property, int defaultValue) {
		var value = System.getProperty(property);
		return Optional.ofNullable(value)
				.map(Integer::parseInt)
				.orElse(defaultValue);
	}
}
