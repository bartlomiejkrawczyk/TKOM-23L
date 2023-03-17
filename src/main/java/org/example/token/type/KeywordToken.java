package org.example.token.type;

import lombok.Value;
import org.example.token.Position;
import org.example.token.Token;
import org.example.token.TokenType;

@Value
public class KeywordToken implements Token {

	TokenType type;
	Position position;

	@SuppressWarnings("unchecked")
	@Override
	public Void getValue() {
		return null;
	}
}
