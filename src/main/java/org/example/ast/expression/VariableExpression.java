package org.example.ast.expression;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;

@Value
public class VariableExpression implements Expression {

	String value;

	public ExpressionType getType() {
		return ExpressionType.VARIABLE;
	}
}
