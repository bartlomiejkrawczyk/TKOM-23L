package org.example.ast.statement;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.Statement;

@ToString(exclude = {"expression"})
@Value
public class ReturnStatement implements Statement {

	Expression expression;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(expression);
	}
}
