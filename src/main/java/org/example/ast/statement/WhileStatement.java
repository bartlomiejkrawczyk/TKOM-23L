package org.example.ast.statement;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.Statement;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@EqualsAndHashCode(exclude = "position")
@Value
public class WhileStatement implements Statement {

	Expression condition;

	Statement body;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(
				condition,
				body
		);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
