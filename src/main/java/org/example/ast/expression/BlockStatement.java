package org.example.ast.expression;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Node;
import org.example.ast.Statement;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@ToString(exclude = {"statements"})
@EqualsAndHashCode(exclude = "position")
@Value
public class BlockStatement implements Statement {

	List<Statement> statements;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return statements.stream()
				.map(Node.class::cast)
				.toList();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
