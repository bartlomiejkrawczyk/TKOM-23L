package org.example.ast.expression;

import java.util.List;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;

@Value
public class FunctionCallExpression implements Expression {

	String function;
	List<Expression> arguments;

	public ExpressionType getType() {
		return ExpressionType.FUNCTION_CALL;
	}
}
