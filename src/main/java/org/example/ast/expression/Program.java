package org.example.ast.expression;

import java.util.List;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;

@Value
public class Program implements Expression {

	List<FunctionExpression> functions;

	@Override
	public ExpressionType getType() {
		return ExpressionType.PROGRAM;
	}
}
