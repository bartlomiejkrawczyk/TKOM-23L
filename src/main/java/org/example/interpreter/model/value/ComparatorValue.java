package org.example.interpreter.model.value;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.example.ast.ValueType;
import org.example.ast.statement.FunctionDefinitionStatement;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.error.ArgumentListDoesNotMatch;
import org.example.interpreter.error.TypesDoNotMatchException;
import org.example.interpreter.model.Value;

@lombok.Value
public class ComparatorValue implements Value {

	TypeDeclaration type;
	Comparator<Value> comparator;

	public ComparatorValue(FunctionDefinitionStatement statement, Comparator<Value> comparator) {
		var arguments = statement.getArguments();
		if (arguments.size() != 2) {
			throw new ArgumentListDoesNotMatch();
		}

		if (!Objects.equals(arguments.get(0).getType(), arguments.get(1).getType())) {
			throw new TypesDoNotMatchException(arguments.get(1).getType(), arguments.get(0).getType());
		}

		this.type = new TypeDeclaration(ValueType.COMPARATOR, List.of(arguments.get(0).getType()));
		this.comparator = comparator;
	}

	@Override
	public Value copy() {
		return this; // comparator is not mutable
	}
}
