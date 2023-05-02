package org.example.ast.statement;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.Statement;
import org.example.ast.expression.Argument;

@ToString(exclude = {"iterable", "body"})
@Value
public class ForStatement implements Statement {

	Argument argument;
	Expression iterable;

	Statement body;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(
				iterable,
				body
		);
	}
}
