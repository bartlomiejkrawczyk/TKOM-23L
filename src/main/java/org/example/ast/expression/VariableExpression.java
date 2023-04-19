package org.example.ast.expression;

import org.example.ast.Expression;
import org.example.ast.Value;

@lombok.Value
public class VariableExpression implements Expression {

	String identifier;
	Value value;
}
