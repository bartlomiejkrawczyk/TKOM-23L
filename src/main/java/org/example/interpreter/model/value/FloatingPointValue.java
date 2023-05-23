package org.example.interpreter.model.value;

import org.example.ast.ValueType;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.model.Value;

@lombok.Value
public class FloatingPointValue implements Value {

	TypeDeclaration type = new TypeDeclaration(ValueType.FLOATING_POINT);
	double floatingPoint;

	@Override
	public int compareTo(Value other) {
		return Double.compare(floatingPoint, other.getFloatingPoint());
	}

	@Override
	public Value copy() {
		return new FloatingPointValue(floatingPoint);
	}
}
