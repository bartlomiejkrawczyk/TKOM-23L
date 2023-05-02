package org.example.ast.expression;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Node;
import org.example.ast.Statement;

@ToString(exclude = {"statements"})
@Value
public class BlockExpression implements Statement {

	List<Statement> statements;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return statements.stream()
				.map(Node.class::cast)
				.toList();
	}
}
