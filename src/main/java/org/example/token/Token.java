package org.example.token;

import java.util.Optional;

public interface Token {
	TokenType getType();

	Position getPosition();

	<T> T getValue();

	default int getPrecedence() {
		return Optional.ofNullable(getType())
				.map(TokenType::getPrecedence)
				.orElse(-1);
	}
}
