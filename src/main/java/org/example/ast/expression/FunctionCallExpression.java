package org.example.ast.expression;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@ToString(exclude = {"arguments"})
@EqualsAndHashCode(exclude = "position")
@Value
public class FunctionCallExpression implements ValueExpression {

	String function;
	List<Expression> arguments;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return arguments.stream()
				.map(Node.class::cast)
				.toList();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
