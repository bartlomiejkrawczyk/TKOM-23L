package org.example.lexer.error;

import org.example.token.Position;

public class UnknownTypeException extends InvalidTokenException {
	public UnknownTypeException(String symbol, Position position) {
		super(String.format("Unknown type of symbol %s at position %s", symbol, position), position);
	}
}
