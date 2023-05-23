package org.example.interpreter.model.value;

import org.example.ast.ValueType;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.model.Value;

@lombok.Value
public class IntegerValue implements Value, Comparable<Value> {

	TypeDeclaration type = new TypeDeclaration(ValueType.INTEGER);
	int integer;

	@Override
	public int compareTo(Value other) {
		return Integer.compare(integer, other.getInteger());
	}

	@Override
	public Value copy() {
		return new IntegerValue(integer);
	}
}
