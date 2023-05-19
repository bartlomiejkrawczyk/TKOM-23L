package org.example.ast.expression.arithmetic;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.expression.ArithmeticExpression;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@AllArgsConstructor
@EqualsAndHashCode(exclude = "position")
@Value
public class NegationArithmeticExpression implements ArithmeticExpression {

	Expression expression;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(expression);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
