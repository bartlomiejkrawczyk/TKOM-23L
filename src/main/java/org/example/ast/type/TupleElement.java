package org.example.ast.type;

import java.util.List;
import lombok.ToString;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.Value;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@ToString(exclude = {"value"})
@lombok.Value
public class TupleElement implements Value {

	String name;
	Expression value;

	@Override
	public Position getPosition() {
		return value.getPosition();
	}

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(
				value
		);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
