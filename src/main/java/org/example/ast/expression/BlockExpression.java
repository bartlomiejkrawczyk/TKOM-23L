package org.example.ast.expression;

import java.util.List;
import lombok.Value;
import org.example.ast.Node;
import org.example.ast.Statement;

@Value
public class BlockExpression implements Statement {

	List<Statement> statements;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return statements.stream()
				.map(it -> (Node) it)
				.toList();
	}
}
