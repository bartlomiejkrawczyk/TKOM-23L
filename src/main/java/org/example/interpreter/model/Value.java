package org.example.interpreter.model;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.error.UnexpectedTypeException;

public interface Value {

	TypeDeclaration getType();

	default int getInteger() {
		throw new UnexpectedTypeException();
	}

	default boolean isBool() {
		throw new UnexpectedTypeException();
	}

	default double getFloatingPoint() {
		throw new UnexpectedTypeException();
	}

	default String getString() {
		throw new UnexpectedTypeException();
	}

	default Comparator<Value> getComparator() {
		throw new UnexpectedTypeException();
	}

	default Optional<Function<List<Value>, Optional<Value>>> findMethod(String method) {
		throw new UnexpectedTypeException();
	}

	default boolean hasNext() {
		throw new UnexpectedTypeException();
	}

	default Value next() {
		throw new UnexpectedTypeException();
	}
}
