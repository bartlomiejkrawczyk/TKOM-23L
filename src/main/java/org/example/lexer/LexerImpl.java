package org.example.lexer;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.example.lexer.error.TokenTooLongException;
import org.example.lexer.error.UnexpectedCharacterException;
import org.example.lexer.error.UnknownTypeException;
import org.example.token.Position;
import org.example.token.Token;
import org.example.token.TokenType;

public class LexerImpl implements Lexer, Closeable {

	private static final String DOT = ".";
	private static final String SINGLE_LINE_COMMENT_CLOSE = "\n";
	private static final String MULTILINE_COMMENT_CLOSE = "*/";
	private static final String SPACE = " ";
	private static final String UNDERSCORE = "_";
	private static final String QUOTATION_MARK = "\"";
	private static final int END_OF_FILE = -1;
	private static final BigDecimal BASE_TEN = BigDecimal.valueOf(10);
	private static final int MAX_IDENTIFIER_LENGTH = 100;
	private static final int MAX_STRING_LENGTH = 1_000;
	private static final Map<String, TokenType> KEYWORDS = EnumSet.allOf(TokenType.class)
			.stream()
			.filter(it -> StringUtils.isNotBlank(it.getKeyword()))
			.filter(it -> StringUtils.isAlphanumeric(it.getKeyword()))
			.collect(Collectors.toMap(TokenType::getKeyword, Function.identity()));
	private static final Map<String, TokenType> SYMBOLS = EnumSet.allOf(TokenType.class)
			.stream()
			.filter(it -> isSymbol(it.getKeyword()))
			.collect(Collectors.toMap(TokenType::getKeyword, Function.identity()));

	private Token.TokenBuilder tokenBuilder;
	private String currentCharacter = SPACE;
	private final PositionalReader reader;

	public LexerImpl(Reader reader) {
		this.reader = new PositionalReader(reader);
	}

	@Override
	public Token nextToken() {
		skipWhitespaces();

		tokenBuilder = Token.builder().position(getPosition());

		if (StringUtils.isEmpty(currentCharacter)) {
			processEndOfFile();
		} else if (StringUtils.equals(currentCharacter, QUOTATION_MARK)) {
			processString();
		} else if (isIdentifierHead(currentCharacter)) {
			processIdentifier();
		} else if (StringUtils.isNumeric(currentCharacter)) {
			processNumber();
		} else if (isSymbol(currentCharacter)) {
			processSymbol();
		} else {
			throw new UnexpectedCharacterException(currentCharacter, getPosition());
		}

		return tokenBuilder.build();
	}

	@SneakyThrows
	private String nextCharacter() {
		var value = reader.read();

		if (value == END_OF_FILE) {
			currentCharacter = StringUtils.EMPTY;
		} else {
			currentCharacter = Character.toString(value);
		}

		return currentCharacter;
	}

	private Position getPosition() {
		return reader.getPosition();
	}

	private Position getTokenPosition() {
		return tokenBuilder.build().getPosition();
	}

	private static boolean isIdentifierHead(String character) {
		return StringUtils.isAlpha(character) || StringUtils.equals(character, UNDERSCORE);
	}

	private static boolean isIdentifierElement(String character) {
		return StringUtils.isAlphanumeric(character) || StringUtils.equals(character, UNDERSCORE);
	}

	private void skipWhitespaces() {
		while (StringUtils.isBlank(currentCharacter) && StringUtils.isNotEmpty(currentCharacter)) {
			nextCharacter();
		}
	}

	private void processEndOfFile() {
		tokenBuilder.type(TokenType.END_OF_FILE);
	}

	private void processString() {
		nextCharacter();
		var content = readUntil(QUOTATION_MARK);
		tokenBuilder.type(TokenType.STRING_CONSTANT).stringValue(content);
	}

	private void processIdentifier() {
		var builder = new StringBuilder();
		builder.append(currentCharacter);

		while (isIdentifierElement(nextCharacter())) {
			builder.append(currentCharacter);
			if (builder.length() > MAX_IDENTIFIER_LENGTH) {
				throw new TokenTooLongException(builder.toString(), getTokenPosition());
			}
		}
		var identifier = builder.toString();

		var type = KEYWORDS.getOrDefault(identifier, TokenType.IDENTIFIER);

		tokenBuilder.type(type).stringValue(identifier);
	}

	private void processNumber() {
		var integerPart = parseInteger();

		if (StringUtils.equals(currentCharacter, DOT)) {
			nextCharacter();

			var fractionalPart = parseFloatingPoint();

			var value = integerPart.add(fractionalPart);
			tokenBuilder.type(TokenType.FLOATING_POINT_CONSTANT).numericalValue(value);
		} else {
			tokenBuilder.type(TokenType.INTEGER_CONSTANT).numericalValue(integerPart);
		}
	}

	private BigDecimal parseInteger() {
		var number = BigDecimal.valueOf(Integer.parseInt(currentCharacter));

		while (StringUtils.isNumeric(nextCharacter())) {
			var integerValue = Integer.parseInt(currentCharacter);
			var currentValue = BigDecimal.valueOf(integerValue);
			number = number.multiply(BASE_TEN).add(currentValue);
		}

		return number;
	}

	private BigDecimal parseFloatingPoint() {
		if (!StringUtils.isNumeric(currentCharacter)) {
			throw new UnexpectedCharacterException(currentCharacter, getTokenPosition());
		}

		var nominator = BigDecimal.valueOf(Integer.parseInt(currentCharacter));
		var denominator = BASE_TEN;

		while (StringUtils.isNumeric(nextCharacter())) {
			var integerValue = Integer.parseInt(currentCharacter);
			var currentValue = BigDecimal.valueOf(integerValue);
			nominator = BASE_TEN.multiply(nominator).add(currentValue);
			denominator = BASE_TEN.multiply(denominator);
		}

		return nominator.divide(denominator, MathContext.UNLIMITED);
	}

	private void processSymbol() {
		var first = currentCharacter;

		var symbol = first;
		if (isSymbol(nextCharacter())) {
			var possibleSymbol = SYMBOLS.keySet()
					.stream()
					.filter(it -> StringUtils.equals(it, first + currentCharacter))
					.findFirst();

			if (possibleSymbol.isPresent()) {
				symbol = possibleSymbol.get();
				nextCharacter();
			}
		}

		var type = SYMBOLS.getOrDefault(symbol, null);

		if (Objects.isNull(type)) {
			throw new UnknownTypeException(symbol, getTokenPosition());
		}

		tokenBuilder.type(type).stringValue(symbol);
		processComments(symbol);
	}

	private static boolean isSymbol(String character) {
		return !StringUtils.isAlphanumeric(character)
				&& StringUtils.isAsciiPrintable(character)
				&& StringUtils.isNotBlank(character);
	}

	private void processComments(String symbol) {
		if (StringUtils.equals(symbol, TokenType.SINGLE_LINE_COMMENT.getKeyword())) {
			var content = readUntil(SINGLE_LINE_COMMENT_CLOSE);
			tokenBuilder.type(TokenType.SINGLE_LINE_COMMENT).stringValue(content);
		} else if (StringUtils.equals(symbol, TokenType.MULTI_LINE_COMMENT.getKeyword())) {
			var content = readUntil(MULTILINE_COMMENT_CLOSE);
			tokenBuilder.type(TokenType.MULTI_LINE_COMMENT).stringValue(content);
		}
	}

	private String readUntil(String enclosingString) {
		var builder = new StringBuilder();

		var patternLength = enclosingString.length();

		String match;
		do {
			builder.append(currentCharacter);
			nextCharacter();

			if (builder.length() > MAX_STRING_LENGTH) {
				throw new TokenTooLongException(builder.toString(), getTokenPosition());
			}

			var start = Math.max(0, builder.length() - patternLength);
			match = builder.substring(start, builder.length());
		} while (!StringUtils.equals(match, enclosingString));


		return builder.substring(0, builder.length() - patternLength);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}
