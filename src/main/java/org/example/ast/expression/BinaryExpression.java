package org.example.ast.expression;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;
import org.example.token.TokenType;

@ToString(exclude = {"leftExpression", "rightExpression"})
@Value
public class BinaryExpression implements Expression {

	TokenType operation;
	Expression leftExpression;
	Expression rightExpression;

	public ExpressionType getType() {
		return ExpressionType.EXPRESSION;
	}

	@Override
	public Iterable<Expression> getChildrenExpressions() {
		return List.of(
				leftExpression,
				rightExpression
		);
	}
}
