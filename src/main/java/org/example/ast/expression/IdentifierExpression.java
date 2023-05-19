package org.example.ast.expression;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@EqualsAndHashCode(exclude = "position")
@Value
public class IdentifierExpression implements ValueExpression {

	String name;

	Position position;

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
