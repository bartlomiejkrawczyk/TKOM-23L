package org.example.interpreter.model;


import java.util.Objects;
import lombok.Builder;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.error.TypesDoNotMatchException;

@Builder
@lombok.Value
public class Variable {

	TypeDeclaration type;

	String identifier;

	Value value;

	public Variable(TypeDeclaration type, String identifier, Value value) {
		this.type = type;
		this.identifier = identifier;
		this.value = value;

		if (!Objects.equals(type, value.getType())) {
			throw new TypesDoNotMatchException(value.getType(), type);
		}
	}
}
