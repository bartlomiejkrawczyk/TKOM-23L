package org.example.interpreter.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Position;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoSuchFunctionException extends CriticalInterpreterException {

	String identifier;

	public NoSuchFunctionException(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public Position getPosition() {
		return null;
	}
}
