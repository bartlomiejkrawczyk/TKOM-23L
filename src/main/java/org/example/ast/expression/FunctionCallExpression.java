package org.example.ast.expression;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;

@ToString(exclude = {"arguments"})
@Value
public class FunctionCallExpression implements ValueExpression {

	String function;
	List<Expression> arguments;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return arguments.stream()
				.map(Node.class::cast)
				.toList();
	}
}
