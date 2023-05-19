package org.example.interpreter.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Position;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DuplicatedVariableException extends CriticalInterpreterException {


	String identifier;

	public DuplicatedVariableException(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public Position getPosition() {
		// TODO: hmm, consider how to refactor this
		return null;
	}
}
