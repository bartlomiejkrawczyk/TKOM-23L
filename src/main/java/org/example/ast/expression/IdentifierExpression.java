package org.example.ast.expression;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.example.token.Position;
import org.example.visitor.Visitor;

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
