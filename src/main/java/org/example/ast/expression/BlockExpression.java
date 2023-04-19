package org.example.ast.expression;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Node;
import org.example.ast.Statement;

@Value
@ToString(exclude = {"statements"})
public class BlockExpression implements Node {

	List<Statement> statements;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return statements.stream()
				.map(it -> (Node) it)
				.toList();
	}
}
