package org.example.interpreter.model.custom;

import lombok.Value;
import org.example.ast.Statement;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@Value
public class PrintFunction implements Statement {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Position getPosition() {
		return new Position(1, 1);
	}
}
