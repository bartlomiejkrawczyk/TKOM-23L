package org.example.lexer.error;

import lombok.NoArgsConstructor;
import org.example.error.PositionalException;
import org.example.token.Position;

@NoArgsConstructor
public abstract class LexerException extends PositionalException {

	protected LexerException(String message) {
		super(message);
	}

	@Override
	public abstract Position getPosition();
}
