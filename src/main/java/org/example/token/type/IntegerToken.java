package org.example.token.type;

import lombok.Value;
import org.example.token.Position;
import org.example.token.Token;
import org.example.token.TokenType;

@Value
public class IntegerToken implements Token {

	Position position;
	Integer value;

	public TokenType getType() {
		return TokenType.INTEGER_CONSTANT;
	}
}
