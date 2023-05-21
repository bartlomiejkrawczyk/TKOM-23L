package org.example.interpreter.model.value;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.error.ArgumentListDoesNotMatch;
import org.example.interpreter.error.TypesDoNotMatchException;
import org.example.interpreter.model.Value;

@AllArgsConstructor
@Builder(toBuilder = true)
@lombok.Value
public class MapValue implements Value {

	TypeDeclaration type;
	Map<Value, Value> map;

	// TODO: add more methods

	Map<String, Function<List<Value>, Value>> mapMethods = Map.of(
			"operator[]", this::get
	);

	public Optional<Function<List<Value>, Value>> findMethod(String method) {
		return Optional.ofNullable(mapMethods.get(method));
	}

	private Value get(List<Value> arguments) {
		if (arguments.size() != 1) {
			throw new ArgumentListDoesNotMatch();
		}
		var key = arguments.get(0);

		if (Objects.equals(key.getType(), type.getTypes().get(0))) {
			throw new TypesDoNotMatchException(key.getType(), type.getTypes().get(0));
		}

		return map.get(key);
	}
}
