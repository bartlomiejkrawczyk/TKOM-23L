package org.example.ast.expression.arithmetic;

import java.util.List;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.expression.ArithmeticExpression;

public interface BinaryArithmeticExpression extends ArithmeticExpression {

	Expression getLeft();

	Expression getRight();

	@Override
	default Iterable<Node> getChildrenExpressions() {
		return List.of(
				getLeft(),
				getRight()
		);
	}
}
