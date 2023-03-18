package org.example.lexer;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.error.ErrorHandler;
import org.example.lexer.error.EndOfFileReachedException;
import org.example.lexer.error.NumericOverflowException;
import org.example.lexer.error.ReaderException;
import org.example.lexer.error.TokenTooLongException;
import org.example.lexer.error.UnexpectedCharacterException;
import org.example.lexer.error.UnknownTypeException;
import org.example.token.Position;
import org.example.token.Token;
import org.example.token.TokenType;
import org.example.token.type.FloatingPointToken;
import org.example.token.type.IntegerToken;
import org.example.token.type.KeywordToken;
import org.example.token.type.StringToken;

@Slf4j
public class LexerImpl implements Lexer {

	private Token token;
	private Position tokenPosition;
	private String currentCharacter = CharactersUtility.SPACE;
	private final PositionalReaderImpl reader;
	private final ErrorHandler errorHandler;

	public LexerImpl(Reader reader, ErrorHandler errorHandler) {
		this.reader = new PositionalReaderImpl(reader);
		this.errorHandler = errorHandler;
	}

	@Override
	public Token nextToken() {
		skipWhitespaces();
		tokenPosition = getPosition();

		if (tryBuildEndOfFile()
				|| tryBuildNumber()
				|| tryBuildIdentifierOrKeyword()
				|| tryBuildString()
				|| tryBuildOperatorOrComment()) {
			return token;
		}

		var exception = new UnexpectedCharacterException(currentCharacter, getPosition());
		errorHandler.handleLexerException(exception);
		nextCharacter();

		return nextToken();
	}

	private String nextCharacter() {
		int value;
		try {
			value = reader.read();
		} catch (IOException error) {
			var exception = new ReaderException(tokenPosition, error.getMessage());
			errorHandler.handleLexerException(exception);
			value = CharactersUtility.END_OF_FILE;
		}

		if (value == CharactersUtility.END_OF_FILE) {
			currentCharacter = StringUtils.EMPTY;
		} else {
			currentCharacter = Character.toString(value);
		}

		return currentCharacter;
	}

	private Position getPosition() {
		return reader.getPosition();
	}

	private void skipWhitespaces() {
		while (LexerUtility.isWhitespace(currentCharacter)) {
			nextCharacter();
		}
	}

	private boolean tryBuildEndOfFile() {
		if (StringUtils.isEmpty(currentCharacter)) {
			token = new KeywordToken(TokenType.END_OF_FILE, tokenPosition);
			return true;
		}
		return false;
	}

	private boolean tryBuildString() {
		if (LexerUtility.STRINGS.containsKey(currentCharacter)) {
			processString();
			return true;
		}
		return false;
	}

	private void processString() {
		var tokenType = LexerUtility.STRINGS.get(currentCharacter);
		nextCharacter();
		var content = readUntil(tokenType.getEnclosingKeyword());
		var unescapedContent = StringEscapeUtils.unescapeJava(content);
		token = new StringToken(tokenType, tokenPosition, unescapedContent);
	}

	private boolean tryBuildIdentifierOrKeyword() {
		if (LexerUtility.isIdentifierHead(currentCharacter)) {
			var identifier = parseIdentifier();
			var type = LexerUtility.KEYWORDS.getOrDefault(identifier, TokenType.IDENTIFIER);
			if (type == TokenType.IDENTIFIER) {
				token = new StringToken(type, tokenPosition, identifier);
			} else {
				token = new KeywordToken(type, tokenPosition);
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("StatementWithEmptyBody")
	private String parseIdentifier() {
		var builder = new StringBuilder();
		builder.append(currentCharacter);

		while (LexerUtility.isIdentifierElement(nextCharacter())) {
			builder.append(currentCharacter);
			if (builder.length() > LexerConfiguration.MAX_IDENTIFIER_LENGTH) {
				var exception = new TokenTooLongException(builder.toString(), tokenPosition);
				errorHandler.handleLexerException(exception);

				while (LexerUtility.isIdentifierElement(nextCharacter())) ;
			}
		}

		return builder.toString();
	}

	private boolean tryBuildNumber() {
		if (LexerUtility.isNumeric(currentCharacter)) {
			processNumber();
			return true;
		}
		return false;
	}

	private void processNumber() {
		var integerPart = parseInteger();

		if (!StringUtils.equals(currentCharacter, CharactersUtility.DOT)) {
			token = new IntegerToken(tokenPosition, integerPart);
		} else {
			nextCharacter();
			var fractionalPart = parseFloatingPoint();
			var value = integerPart + fractionalPart;
			token = new FloatingPointToken(tokenPosition, value);
		}
	}

	private int parseInteger() {
		var number = LexerUtility.parseNumericValue(currentCharacter);

		var previous = number;
		try {
			while (LexerUtility.isNumeric(nextCharacter())) {
				var currentValue = LexerUtility.parseNumericValue(currentCharacter);
				previous = number;
				number = Math.multiplyExact(number, LexerConfiguration.BASE_TEN);
				number = Math.addExact(number, currentValue);
			}
		} catch (ArithmeticException ignore) {
			var exception = new NumericOverflowException(tokenPosition);
			errorHandler.handleLexerException(exception);
			return previous;
		}

		return number;
	}

	@SuppressWarnings("StatementWithEmptyBody")
	private double parseFloatingPoint() {
		if (!LexerUtility.isNumeric(currentCharacter)) {
			var exception = new UnexpectedCharacterException(currentCharacter, tokenPosition);
			errorHandler.handleLexerException(exception);
			return 0;
		}

		var nominator = (long) LexerUtility.parseNumericValue(currentCharacter);
		var denominator = (long) LexerConfiguration.BASE_TEN;

		try {
			while (LexerUtility.isNumeric(nextCharacter())) {
				denominator = Math.multiplyExact(LexerConfiguration.BASE_TEN, denominator);
				var currentValue = LexerUtility.parseNumericValue(currentCharacter);
				nominator = LexerConfiguration.BASE_TEN * nominator + currentValue;
			}
		} catch (ArithmeticException ignore) {
			var exception = new NumericOverflowException(tokenPosition);
			errorHandler.handleLexerException(exception);
			while (LexerUtility.isNumeric(nextCharacter())) ;
		}

		return (double) nominator / denominator;
	}

	private boolean tryBuildOperatorOrComment() {
		if (LexerUtility.isSymbol(currentCharacter)) {
			processOperatorOrComment();
			return true;
		}
		return false;
	}

	private void processOperatorOrComment() {
		var first = currentCharacter;
		var symbol = first;
		if (LexerUtility.isSymbol(nextCharacter())) {
			var possibleSymbol = LexerUtility.TWO_LETTER_SYMBOLS.keySet()
					.stream()
					.filter(it -> StringUtils.equals(it, first + currentCharacter))
					.findFirst();

			if (possibleSymbol.isPresent()) {
				symbol = possibleSymbol.get();
				nextCharacter();
			}
		}

		var type = LexerUtility.SYMBOLS.getOrDefault(symbol, null);

		if (Objects.isNull(type)) {
			var exception = new UnknownTypeException(symbol, tokenPosition);
			errorHandler.handleLexerException(exception);
			token = nextToken();
		} else if (LexerUtility.COMMENTS.containsKey(symbol)) {
			processComments(LexerUtility.COMMENTS.get(symbol));
		} else {
			token = new KeywordToken(type, tokenPosition);
		}
	}

	private void processComments(TokenType commentType) {
		var content = readUntil(commentType.getEnclosingKeyword());
		token = new StringToken(commentType, tokenPosition, content);
	}

	private String readUntil(String enclosingString) {
		var builder = new StringBuilder();
		var patternLength = enclosingString.length();

		String match;
		do {
			builder.append(currentCharacter);

			if (LexerUtility.isEndOfFile(currentCharacter)) {
				var exception = new EndOfFileReachedException(enclosingString, tokenPosition);
				errorHandler.handleLexerException(exception);
				return builder.toString();
			}

			if (builder.length() > LexerConfiguration.MAX_STRING_LENGTH + patternLength) {
				var exception = new TokenTooLongException(builder.toString(), tokenPosition);
				errorHandler.handleLexerException(exception);
				var start = Math.max(0, builder.length() - patternLength);
				passUntil(builder.substring(start), enclosingString);
				return builder.toString();
			}

			nextCharacter();

			var start = Math.max(0, builder.length() - patternLength);
			match = builder.substring(start);
		} while (!StringUtils.equals(match, enclosingString));

		return builder.substring(0, builder.length() - patternLength);
	}

	private void passUntil(String beginning, String enclosingString) {
		var characters = beginning.chars().boxed().toList();
		var queue = new LinkedList<>(characters);
		var pattern = enclosingString.codePoints().boxed().toList();

		do {
			if (LexerUtility.isEndOfFile(currentCharacter)) {
				var exception = new EndOfFileReachedException(enclosingString, tokenPosition);
				errorHandler.handleLexerException(exception);
				return;
			}

			queue.addLast(currentCharacter.codePointAt(0));
			queue.pollFirst();

			nextCharacter();
		} while (!queue.equals(pattern));
	}
}
