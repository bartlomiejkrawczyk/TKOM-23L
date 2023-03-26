package org.example.ast.expression;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;


@ToString(exclude = {"prototype", "body"})
@Value
public class FunctionExpression implements Expression {

	PrototypeExpression prototype;
	Expression body;

	public ExpressionType getType() {
		return ExpressionType.FUNCTION;
	}

	@Override
	public Iterable<Expression> getChildrenExpressions() {
		return List.of(
				prototype,
				body
		);
	}
}
