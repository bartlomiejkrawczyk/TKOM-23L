package org.example.lexer;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.example.token.TokenType;

@UtilityClass
public class LexerUtility {

	public static final Map<String, TokenType> KEYWORDS = EnumSet.allOf(TokenType.class)
			.stream()
			.filter(it -> StringUtils.isNotBlank(it.getKeyword()))
			.filter(it -> StringUtils.isAlphanumeric(it.getKeyword()))
			.collect(Collectors.toUnmodifiableMap(TokenType::getKeyword, Function.identity()));
	public static final Map<String, TokenType> SYMBOLS = EnumSet.allOf(TokenType.class)
			.stream()
			.filter(it -> isSymbol(it.getKeyword()))
			.collect(Collectors.toUnmodifiableMap(TokenType::getKeyword, Function.identity()));

	public static final Map<String, TokenType> OPERATORS = SYMBOLS.entrySet()
			.stream()
			.filter(entry -> Objects.isNull(entry.getValue().getEnclosingKeyword()))
			.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

	public static final Map<String, TokenType> TWO_LETTER_SYMBOLS = SYMBOLS.entrySet()
			.stream()
			.filter(entry -> entry.getKey().length() == 2)
			.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

	public static final Map<String, TokenType> COMMENTS = Stream.of(
					TokenType.SINGLE_LINE_COMMENT,
					TokenType.MULTI_LINE_COMMENT
			)
			.collect(Collectors.toUnmodifiableMap(TokenType::getKeyword, Function.identity()));

	public static final Map<String, TokenType> STRINGS = Stream.of(
					TokenType.STRING_DOUBLE_QUOTE_CONSTANT,
					TokenType.STRING_SINGLE_QUOTE_CONSTANT
			)
			.collect(Collectors.toUnmodifiableMap(TokenType::getKeyword, Function.identity()));

	public static boolean isWhitespace(String character) {
		return StringUtils.isBlank(character) && StringUtils.isNotEmpty(character);
	}

	public static boolean isEndOfFile(String character) {
		return StringUtils.isEmpty(character);
	}

	public static boolean isNumeric(String character) {
		return StringUtils.isNumeric(character);
	}

	public static int parseNumericValue(String character) {
		return Character.getNumericValue(character.charAt(0));
	}

	public static boolean isSymbol(String character) {
		return !StringUtils.isAlphanumeric(character)
				&& StringUtils.isAsciiPrintable(character)
				&& StringUtils.isNotBlank(character);
	}

	public static boolean isIdentifierElement(String character) {
		return StringUtils.isAlphanumeric(character) || StringUtils.equals(character, CharactersUtility.UNDERSCORE);
	}

	public static boolean isIdentifierHead(String character) {
		return StringUtils.isAlpha(character) || StringUtils.equals(character, CharactersUtility.UNDERSCORE);
	}
}
