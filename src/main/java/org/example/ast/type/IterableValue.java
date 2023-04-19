package org.example.ast.type;

import org.example.ast.Value;

@lombok.Value
public class IterableValue<T> implements Value {

	Iterable<T> value;
}
