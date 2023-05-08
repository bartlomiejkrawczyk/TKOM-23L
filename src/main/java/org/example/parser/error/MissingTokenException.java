package org.example.parser.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Token;
import org.example.token.TokenType;

@EqualsAndHashCode(callSuper = true)
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MissingTokenException extends CriticalParserException {

	TokenType type;

	public MissingTokenException(Token token, TokenType type) {
		super("Expected token: " + type, token);
		this.type = type;
	}
}
