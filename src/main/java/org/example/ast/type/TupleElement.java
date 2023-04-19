package org.example.ast.type;

import lombok.Value;

@Value
public class TupleElement {

	String identifier;
	Object value;

	@SuppressWarnings("unchecked")
	<T> T getValue() {
		return (T) value;
	}
}
