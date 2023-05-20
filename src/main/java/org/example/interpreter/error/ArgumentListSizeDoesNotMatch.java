package org.example.interpreter.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.ast.expression.FunctionCallExpression;
import org.example.token.Position;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ArgumentListSizeDoesNotMatch extends CriticalInterpreterException {

	FunctionCallExpression expression;

	public ArgumentListSizeDoesNotMatch(FunctionCallExpression expression) {
		this.expression = expression;
	}

	@Override
	public Position getPosition() {
		return null;
	}
}
