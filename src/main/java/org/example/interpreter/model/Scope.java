package org.example.interpreter.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.example.interpreter.error.DuplicatedVariableException;
import org.example.interpreter.error.TypesDoNotMatchException;

@lombok.Value
public class Scope {

	Map<String, Variable> localVariables = new HashMap<>();

	public Optional<Variable> findVariable(String identifier) {
		return Optional.ofNullable(localVariables.get(identifier));
	}

	public void addVariable(Variable variable) {
		if (localVariables.containsKey(variable.getIdentifier())) {
			throw new DuplicatedVariableException(variable.getIdentifier());
		}
		localVariables.put(variable.getIdentifier(), variable);
	}

	public boolean updateVariable(String identifier, Value value) {
		if (!localVariables.containsKey(identifier)) {
			return false;
		}
		var previous = localVariables.get(identifier);

		if (previous.getType() != value.getType()) {
			throw new TypesDoNotMatchException(value.getType(), previous.getType());
		}

		localVariables.put(identifier, new Variable(previous.getType(), previous.getIdentifier(), value));
		return true;
	}
}
