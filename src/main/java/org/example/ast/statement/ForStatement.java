package org.example.ast.statement;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.Statement;
import org.example.ast.expression.Argument;
import org.example.token.Position;
import org.example.visitor.Visitor;

@ToString(exclude = {"iterable", "body"})
@EqualsAndHashCode(exclude = "position")
@Value
public class ForStatement implements Statement {

	Argument argument;
	Expression iterable;

	Statement body;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(
				iterable,
				body
		);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
