package org.example.ast.statement;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.Statement;
import org.example.token.Position;
import org.example.visitor.Visitor;

@ToString(exclude = {"value"})
@EqualsAndHashCode(exclude = "position")
@Value
public class AssignmentStatement implements Statement {

	String name;
	Expression value;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(value);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
