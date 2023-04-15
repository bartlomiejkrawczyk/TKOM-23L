package org.example.parser.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Token;
import org.example.token.TokenType;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@ToString
public class UnexpectedTokenException extends ParserException {

	TokenType expectedType;
	Token token;

	public UnexpectedTokenException(TokenType expectedType, Token token) {
		super(String.format("UnexpectedToken: %s at position position %s%nExpected: %s", token, token.getPosition().toPositionString(),
				expectedType));
		this.expectedType = expectedType;
		this.token = token;
	}
}
