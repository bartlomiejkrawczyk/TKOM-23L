package org.example.ast.expression;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;

@Value
public class FloatingPointExpression implements Expression {

	Double value;

	public ExpressionType getType() {
		return ExpressionType.FLOATING_POINT_VALUE;
	}
}
