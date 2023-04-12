package org.example.ast.expression;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;

@Value
public class DeclarationExpression implements Expression {

	@Override
	public ExpressionType getType() {
		return ExpressionType.DECLARATION;
	}
}
