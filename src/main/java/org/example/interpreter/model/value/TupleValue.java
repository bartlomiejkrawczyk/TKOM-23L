package org.example.interpreter.model.value;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.model.Value;

@lombok.Value
public class TupleValue implements Value {

	TypeDeclaration type;
	Map<String, Value> tuple;

	public Optional<Value> get(String identifier) {
		return Optional.ofNullable(tuple.get(identifier));
	}

	@Override
	public Value copy() {
		var result = tuple.entrySet()
				.stream()
				.map(it -> Map.entry(it.getKey(), it.getValue().copy()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		return new TupleValue(type, result);
	}
}
