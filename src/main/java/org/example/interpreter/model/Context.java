package org.example.interpreter.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.token.Position;

@RequiredArgsConstructor
public class Context {

	@Getter
	private final String function;

	@Getter
	private final Position position;

	private final Deque<Scope> scopes = new ArrayDeque<>(List.of(new Scope()));

	public void incrementScope() {
		scopes.add(new Scope());
	}

	public void decrementScope() {
		scopes.removeLast();
	}

	public void addVariable(Variable variable) {
		var scope = scopes.getLast();
		scope.addVariable(variable);
	}

	public boolean updateVariable(String identifier, Value value) {
		var iterator = scopes.descendingIterator();
		while (iterator.hasNext()) {
			var scope = iterator.next();
			if (scope.updateVariable(identifier, value)) {
				return true;
			}
		}
		return false;
	}

	public Optional<Variable> findVariable(String identifier) {
		var iterator = scopes.descendingIterator();
		while (iterator.hasNext()) {
			var scope = iterator.next();
			var variable = scope.findVariable(identifier);
			if (variable.isPresent()) {
				return variable;
			}
		}
		return Optional.empty();
	}
}
