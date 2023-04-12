package org.example.ast.expression;

import java.util.List;
import java.util.Map;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;
import org.example.ast.ValueType;


@ToString(exclude = {"body"})
@Value
public class FunctionDefinitionExpression implements Expression {

	String name;
	// TODO: Consider instead of ValueType using String to allow for user defined types
	Map<String, ValueType> arguments;
	Expression body;

	public ExpressionType getType() {
		return ExpressionType.FUNCTION_DECLARATION;
	}

	@Override
	public Iterable<Expression> getChildrenExpressions() {
		return List.of(
				body
		);
	}
}
