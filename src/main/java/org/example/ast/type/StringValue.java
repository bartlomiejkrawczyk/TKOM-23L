package org.example.ast.type;

import lombok.EqualsAndHashCode;
import org.example.ast.Value;
import org.example.token.Position;
import org.example.visitor.Visitor;

@EqualsAndHashCode(exclude = "position")
@lombok.Value
public class StringValue implements Value {

	String value;

	Position position;

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
