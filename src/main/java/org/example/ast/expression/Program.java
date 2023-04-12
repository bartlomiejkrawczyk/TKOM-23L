package org.example.ast.expression;

import java.util.List;
import java.util.Map;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;

@Value
public class Program implements Expression {

	Map<String, FunctionDefinitionExpression> functionDefinitions;
	List<DeclarationExpression> declarations;

	@Override
	public ExpressionType getType() {
		return ExpressionType.PROGRAM;
	}
}
