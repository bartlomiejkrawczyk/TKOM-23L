package org.example.ast.expression;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;
import org.example.token.TokenType;

@Value
public class BinaryExpression implements Expression {

	TokenType operation;
	Expression leftExpression;
	Expression rightExpression;

	public ExpressionType getType() {
		return ExpressionType.EXPRESSION;
	}
}
