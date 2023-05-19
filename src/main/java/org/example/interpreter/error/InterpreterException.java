package org.example.interpreter.error;

import lombok.NoArgsConstructor;
import org.example.error.PositionalException;

@NoArgsConstructor
public abstract class InterpreterException extends PositionalException {

	protected InterpreterException(String message) {
		super(message);
	}
}
