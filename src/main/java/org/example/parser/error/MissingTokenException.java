package org.example.parser.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Token;
import org.example.token.TokenType;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MissingTokenException extends CriticalParserException {

	TokenType expected;

	public MissingTokenException(Token token, TokenType expected) {
		super("Missing token", token);
		this.expected = expected;
	}
}
