package org.example.interpreter.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.ast.type.TypeDeclaration;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TypesDoNotMatchException extends CriticalInterpreterException {

	TypeDeclaration provided;
	TypeDeclaration expected;
}
