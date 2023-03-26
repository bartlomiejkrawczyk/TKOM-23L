package org.example.lexer;

import lombok.experimental.UtilityClass;
import org.example.config.Configuration;

@UtilityClass
public class LexerConfiguration extends Configuration {

	public static final int BASE_TEN = 10;
	public static final int MAX_IDENTIFIER_LENGTH = getProperty("max.identifier.length", 100);
	public static final int MAX_STRING_LENGTH = getProperty("max.string.length", 1_000);
}
