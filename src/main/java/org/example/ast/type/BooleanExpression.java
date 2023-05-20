package org.example.ast.type;

import lombok.EqualsAndHashCode;
import org.example.ast.Value;
import org.example.ast.expression.LogicalExpression;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@EqualsAndHashCode(exclude = "position")
@lombok.Value
public class BooleanExpression implements Value, LogicalExpression {

	Boolean value;

	Position position;

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
