package org.example.interpreter.model.value;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.ast.ValueType;
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

	Map<String, Function<List<Value>, Optional<Value>>> mapMethods = Map.of(
			"operator[]", this::get,
			"retrieveValue", this::retrieveValue,
			"put", this::put,
			"contains", this::contains,
			"remove", this::remove,
			"iterable", this::iterable,
			"sortedIterable", this::sortedIterable
	);

	@Override
	public Optional<Function<List<Value>, Optional<Value>>> findMethod(String method) {
		return Optional.ofNullable(mapMethods.get(method));
	}

	private Optional<Value> get(List<Value> arguments) {
		validateArguments(arguments, List.of(getKeyType()));
		var key = arguments.get(0);
		return Optional.ofNullable(map.get(key));
	}

	private Optional<Value> retrieveValue(List<Value> arguments) {
		validateArguments(arguments, List.of(getKeyType()));
		var key = arguments.get(0);
		return Optional.ofNullable(map.get(key).copy());
	}

	private Optional<Value> put(List<Value> arguments) {
		validateArguments(arguments, List.of(getKeyType(), getValueType()));
		var key = arguments.get(0);
		var value = arguments.get(1);
		map.put(key, value);
		return Optional.empty();
	}

	private Optional<Value> contains(List<Value> arguments) {
		validateArguments(arguments, List.of(getKeyType()));
		var key = arguments.get(0);
		var result = map.containsKey(key);
		return Optional.of(new BooleanValue(result));
	}

	private Optional<Value> remove(List<Value> arguments) {
		validateArguments(arguments, List.of(getKeyType()));
		var key = arguments.get(0);
		map.remove(key);
		return Optional.empty();
	}

	public Optional<Value> iterable(List<Value> arguments) {
		validateArguments(arguments, List.of());
		return Optional.of(iterable());
	}

	public IterableValue iterable() {
		var tupleType = new TypeDeclaration(ValueType.TUPLE, List.of(getKeyType(), getValueType()));
		var result = map.entrySet()
				.stream()
				.map(it -> new TupleValue(
						tupleType,
						Map.of("key", it.getKey(), "value", it.getValue()))
				)
				.map(Value.class::cast)
				.toList();
		return new IterableValue(
				new TypeDeclaration(ValueType.ITERABLE, List.of(tupleType)),
				result
		);
	}

	private Optional<Value> sortedIterable(List<Value> arguments) {
		validateArguments(
				arguments,
				List.of(new TypeDeclaration(ValueType.COMPARATOR,
								List.of(
										new TypeDeclaration(ValueType.TUPLE, List.of(getKeyType(), getValueType())
										)
								)
						)
				)
		);
		var comparator = arguments.get(0);
		var tupleType = new TypeDeclaration(ValueType.TUPLE, List.of(getKeyType(), getValueType()));
		var result = map.entrySet()
				.stream()
				.map(it -> new TupleValue(
						tupleType,
						Map.of("key", it.getKey(), "value", it.getValue()))
				)
				.map(Value.class::cast)
				.sorted(comparator.getComparator())
				.toList();
		return Optional.of(
				new IterableValue(
						new TypeDeclaration(ValueType.ITERABLE, List.of(tupleType)),
						result
				)
		);
	}

	private TypeDeclaration getKeyType() {
		return type.getTypes().get(0);
	}

	private TypeDeclaration getValueType() {
		return type.getTypes().get(1);
	}

	private void validateArguments(List<Value> arguments, List<TypeDeclaration> expected) {
		if (arguments.size() != expected.size()) {
			throw new ArgumentListDoesNotMatch();
		}
		for (var i = 0; i < arguments.size(); i++) {
			if (!Objects.equals(arguments.get(i).getType(), expected.get(i))) {
				throw new TypesDoNotMatchException(arguments.get(i).getType(), expected.get(i));
			}
		}
	}

	@Override
	public Value copy() {
		var result = map.entrySet()
				.stream()
				.map(it -> Map.entry(it.getKey().copy(), it.getValue().copy()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		return new MapValue(type, result);
	}
}
