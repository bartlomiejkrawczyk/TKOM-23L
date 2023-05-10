package org.example.ast.expression;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Node;
import org.example.ast.Statement;
import org.example.token.Position;

@ToString(exclude = {"statements"})
@EqualsAndHashCode(exclude = "position")
@Value
public class BlockExpression implements Statement {

	List<Statement> statements;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return statements.stream()
				.map(Node.class::cast)
				.toList();
	}
}
