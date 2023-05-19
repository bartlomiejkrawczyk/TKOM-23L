package org.example.ast.expression;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.token.Position;
import org.example.visitor.Visitor;

@ToString(exclude = {"object"})
@EqualsAndHashCode(exclude = "position")
@Value
public class TupleCallExpression implements ValueExpression {

	Expression object;
	String identifier;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(object);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
