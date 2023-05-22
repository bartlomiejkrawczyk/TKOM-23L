package org.example.interpreter.model.value;

import java.util.Iterator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.model.Value;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IterableValue implements Value, Iterator<Value> {

	TypeDeclaration type;
	final Iterator<Value> iterator;

	public IterableValue(TypeDeclaration type, Iterable<Value> iterable) {
		this.type = type;
		this.iterator = iterable.iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Value next() {
		return iterator.next();
	}
}
