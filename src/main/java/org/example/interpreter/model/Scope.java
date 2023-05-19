package org.example.interpreter.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Value;
import org.example.interpreter.error.DuplicatedVariableException;

@Value
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
}
