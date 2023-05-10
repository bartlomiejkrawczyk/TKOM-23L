package org.example.ast.expression;

import lombok.EqualsAndHashCode;
import org.example.ast.Expression;
import org.example.ast.Value;
import org.example.token.Position;

@EqualsAndHashCode(exclude = "position")
@lombok.Value
public class VariableExpression implements Expression {

	String identifier;
	Value value;

	Position position;
}
