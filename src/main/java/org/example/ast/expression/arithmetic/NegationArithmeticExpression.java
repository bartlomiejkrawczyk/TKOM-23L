package org.example.ast.expression.arithmetic;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Node;
import org.example.ast.expression.ArithmeticExpression;

@ToString(exclude = "expression")
@Value
public class NegationArithmeticExpression implements ArithmeticExpression {

	ArithmeticExpression expression;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(expression);
	}
}
