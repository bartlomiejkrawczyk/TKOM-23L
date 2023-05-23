package org.example.interpreter.model.value;

import org.example.ast.ValueType;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.model.Value;

@lombok.Value
public class StringValue implements Value {

	TypeDeclaration type = new TypeDeclaration(ValueType.STRING);
	String string;

	@Override
	public Value copy() {
		return new StringValue(string);
	}
}
