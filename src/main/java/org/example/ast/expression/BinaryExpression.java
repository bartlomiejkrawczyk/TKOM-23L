package org.example.ast.expression;

import java.util.List;
import org.example.ast.Expression;
import org.example.ast.Node;

public interface BinaryExpression extends Expression {

	Expression getLeft();

	Expression getRight();

	@Override
	default Iterable<Node> getChildrenExpressions() {
		return List.of(getLeft(), getRight());
	}
}
