package org.example.ast.type;

import lombok.EqualsAndHashCode;
import org.example.ast.Value;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@EqualsAndHashCode(exclude = "position")
@lombok.Value
public class StringExpression implements Value {

	String value;

	Position position;

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
