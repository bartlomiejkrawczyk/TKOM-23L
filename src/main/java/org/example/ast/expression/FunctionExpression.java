package org.example.ast.expression;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;

@Value
public class FunctionExpression implements Expression {

	PrototypeExpression prototype;
	Expression body;

	public ExpressionType getType() {
		return ExpressionType.FUNCTION;
	}
}
