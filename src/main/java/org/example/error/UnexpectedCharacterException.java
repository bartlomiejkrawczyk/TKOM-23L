package org.example.error;

import org.example.token.Position;

public class UnexpectedCharacterException extends InvalidTokenException {
	public UnexpectedCharacterException(String character, Position position) {
		super(String.format("Unexpected character %s at position %s", character, position), position);
	}
}
