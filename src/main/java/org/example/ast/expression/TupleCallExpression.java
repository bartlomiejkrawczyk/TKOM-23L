package org.example.ast.expression;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;

@ToString(exclude = {"object"})
@Value
public class TupleCallExpression implements ValueExpression {

	Expression object;
	String identifier;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(object);
	}
}
