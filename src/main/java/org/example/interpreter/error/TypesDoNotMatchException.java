package org.example.interpreter.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.ast.type.TypeDeclaration;
import org.example.token.Position;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TypesDoNotMatchException extends CriticalInterpreterException {

	TypeDeclaration provided;
	TypeDeclaration expected;

	public TypesDoNotMatchException(TypeDeclaration provided, TypeDeclaration expected) {
		this.provided = provided;
		this.expected = expected;
	}

	@Override
	public Position getPosition() {
		return null;
	}
}
