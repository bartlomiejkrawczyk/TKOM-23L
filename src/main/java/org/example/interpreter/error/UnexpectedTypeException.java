package org.example.interpreter.error;


import org.example.token.Position;

public class UnexpectedTypeException extends CriticalInterpreterException {
	@Override
	public Position getPosition() {
		return null;
	}
}
