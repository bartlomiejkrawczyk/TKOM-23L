package org.example.ast.expression;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.token.Position;
import org.example.visitor.Visitor;

@ToString(exclude = {"object", "function"})
@EqualsAndHashCode(exclude = "position")
@Value
public class MethodCallExpression implements ValueExpression {

	Expression object;

	FunctionCallExpression function;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(object, function);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
