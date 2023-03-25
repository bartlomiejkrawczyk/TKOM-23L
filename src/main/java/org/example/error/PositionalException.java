package org.example.error;

import lombok.NoArgsConstructor;
import org.example.token.Position;

@NoArgsConstructor
public abstract class PositionalException extends RuntimeException {

	protected PositionalException(String message) {
		super(message);
	}

	public abstract Position getPosition();
}
