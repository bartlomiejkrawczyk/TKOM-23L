package org.example.interpreter.model;

import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.error.UnexpectedTypeException;

public interface Value {

	TypeDeclaration getType();

	default int getInteger() {
		throw new UnexpectedTypeException();
	}

	default boolean getBool() {
		throw new UnexpectedTypeException();
	}

	default double getFloatingPoint() {
		throw new UnexpectedTypeException();
	}

	default String getString() {
		throw new UnexpectedTypeException();
	}

	//	default MapExpression getMap() {
	//		throw new UnexpectedTypeException();
	//	}
	//
	//	default TupleExpression getTuple() {
	//		throw new UnexpectedTypeException();
	//	}

	//	default Iterable getIterable() {
	//		throw new UnexpectedTypeException();
	//	}

	//	default Comparator getComparator() {
	//		throw new UnexpectedTypeException();
	//	}
}
