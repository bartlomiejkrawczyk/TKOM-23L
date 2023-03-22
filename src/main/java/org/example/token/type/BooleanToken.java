package org.example.token.type;

import lombok.Value;
import org.example.token.Position;
import org.example.token.Token;
import org.example.token.TokenType;

@Value
public class BooleanToken implements Token {

	Position position;
	Boolean value;

	public TokenType getType() {
		return TokenType.BOOLEAN_CONSTANT;
	}
}
