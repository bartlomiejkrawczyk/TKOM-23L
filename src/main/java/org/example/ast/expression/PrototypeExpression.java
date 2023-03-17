package org.example.ast.expression;

import java.util.Map;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.ExpressionType;
import org.example.ast.ValueType;

@Value
public class PrototypeExpression implements Expression {

	String name;
	// TODO: Consider instead of ValueType using String to allow for user defined types
	Map<String, ValueType> arguments;

	public ExpressionType getType() {
		return ExpressionType.FUNCTION_PROTOTYPE;
	}
}
