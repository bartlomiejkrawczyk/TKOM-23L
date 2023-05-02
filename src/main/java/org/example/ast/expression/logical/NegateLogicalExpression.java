package org.example.ast.expression.logical;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.expression.LogicalExpression;

@ToString(exclude = {"expression"})
@Value
public class NegateLogicalExpression implements LogicalExpression {

	Expression expression;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(expression);
	}
}
