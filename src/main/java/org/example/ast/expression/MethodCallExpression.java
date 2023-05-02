package org.example.ast.expression;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;

@ToString(exclude = {"object", "function"})
@Value
public class MethodCallExpression implements ValueExpression {

	Expression object;

	FunctionCallExpression function;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(object, function);
	}
}
