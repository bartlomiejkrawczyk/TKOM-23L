package org.example.ast.expression;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;

@Value
public class IntegerExpression implements Expression {
	Integer value;

	public ExpressionType getType() {
		return ExpressionType.INTEGER_VALUE;
	}
}
