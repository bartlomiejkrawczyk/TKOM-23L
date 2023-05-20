package org.example.interpreter.model.value;

import org.example.ast.ValueType;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.model.Value;

@lombok.Value
public class BooleanValue implements Value {

	TypeDeclaration type = new TypeDeclaration(ValueType.BOOLEAN);
	boolean bool;
}
