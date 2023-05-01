package org.example.ast.expression.arithmetic;

import java.util.List;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.expression.ArithmeticExpression;

@Value
public class NegationArithmeticExpression implements ArithmeticExpression {

	Expression expression;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(expression);
	}
}
