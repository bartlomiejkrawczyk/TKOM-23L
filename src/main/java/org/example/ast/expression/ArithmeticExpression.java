package org.example.ast.expression;

import java.util.stream.Stream;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.token.TokenType;

@ToString(exclude = {"leftExpression", "rightExpression"})
@Value
public class ArithmeticExpression implements Expression {

	TokenType operation;
	Expression leftExpression;
	Expression rightExpression;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return Stream.of(
						leftExpression,
						rightExpression
				).map(it -> (Node) it)
				.toList();
	}
}
