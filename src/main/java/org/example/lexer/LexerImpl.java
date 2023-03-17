package org.example.lexer;

import java.io.Reader;
import java.util.Objects;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.error.ErrorHandler;
import org.example.error.ErrorHandlerImpl;
import org.example.lexer.error.EndOfFileReachedException;
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

public class LexerImpl implements Lexer {

	private Token token;
	private Position tokenPosition;
	private String currentCharacter = CharactersUtility.SPACE;
	private final PositionalReaderImpl reader;
	private final ErrorHandler errorHandler;

	public LexerImpl(Reader reader) {
		this.reader = new PositionalReaderImpl(reader);
		this.errorHandler = new ErrorHandlerImpl();
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

		return nextToken();
	}

	@SneakyThrows
	private String nextCharacter() {
		var value = reader.read();

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

	@SuppressWarnings({"checkstyle:NeedBraces", "CheckStyle", "StatementWithEmptyBody"})
	private String parseIdentifier() {
		var builder = new StringBuilder();
		builder.append(currentCharacter);

		while (LexerUtility.isIdentifierElement(nextCharacter())) {
			builder.append(currentCharacter);
			if (builder.length() > LexerConfiguration.MAX_IDENTIFIER_LENGTH) {
				var error = new TokenTooLongException(builder.toString(), tokenPosition);
				errorHandler.handleLexerException(error);

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
			token = new IntegerToken(TokenType.INTEGER_CONSTANT, tokenPosition, integerPart);
		} else {
			nextCharacter();
			var fractionalPart = parseFloatingPoint();
			var value = integerPart + fractionalPart;
			token = new FloatingPointToken(TokenType.FLOATING_POINT_CONSTANT, tokenPosition, value);
		}
	}

	private int parseInteger() {
		var number = LexerUtility.parseNumericValue(currentCharacter);

		while (StringUtils.isNumeric(nextCharacter())) {
			var currentValue = LexerUtility.parseNumericValue(currentCharacter);
			number = number * LexerConfiguration.BASE_TEN + currentValue;
		}

		return number;
	}

	private double parseFloatingPoint() {
		if (!StringUtils.isNumeric(currentCharacter)) {
			var exception = new UnexpectedCharacterException(currentCharacter, tokenPosition);
			errorHandler.handleLexerException(exception);
			return 0;
		}

		var nominator = LexerUtility.parseNumericValue(currentCharacter);
		var denominator = LexerConfiguration.BASE_TEN;

		while (StringUtils.isNumeric(nextCharacter())) {
			var currentValue = Integer.parseInt(currentCharacter);
			nominator = LexerConfiguration.BASE_TEN * nominator + currentValue;
			denominator = LexerConfiguration.BASE_TEN * denominator;
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
			var error = new UnknownTypeException(symbol, tokenPosition);
			errorHandler.handleLexerException(error);
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

			if (builder.length() > LexerConfiguration.MAX_STRING_LENGTH) {
				var exception = new TokenTooLongException(builder.toString(), tokenPosition);
				errorHandler.handleLexerException(exception);
				// TODO: skip the rest till you find pattern
			}

			nextCharacter();

			var start = Math.max(0, builder.length() - patternLength);
			match = builder.substring(start, builder.length());
		} while (!StringUtils.equals(match, enclosingString));

		return builder.substring(0, builder.length() - patternLength);
	}
}
