package org.example.token.type;

import lombok.Value;
import org.example.token.Position;
import org.example.token.Token;
import org.example.token.TokenType;

@Value
public class FloatingPointToken implements Token {

	Position position;
	Double value;

	public TokenType getType() {
		return TokenType.FLOATING_POINT_CONSTANT;
	}
}
