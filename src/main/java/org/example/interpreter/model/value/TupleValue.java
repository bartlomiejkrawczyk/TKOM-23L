package org.example.interpreter.model.value;

import java.util.Map;
import java.util.Optional;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.model.Value;

@lombok.Value
public class TupleValue implements Value {

	TypeDeclaration type;
	Map<String, Value> tuple;

	public Optional<Value> get(String identifier) {
		return Optional.ofNullable(tuple.get(identifier));
	}
}
