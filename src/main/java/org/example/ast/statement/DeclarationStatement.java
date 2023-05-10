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

@ToString(exclude = {"expression"})
@EqualsAndHashCode(exclude = "position")
@Value
public class DeclarationStatement implements Statement {

	Argument argument;
	Expression expression;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(expression);
	}
}
