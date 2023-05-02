package org.example.ast.statement;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.Statement;

@ToString(exclude = {"condition", "ifTrue", "ifFalse"})
@Value
public class IfStatement implements Statement {

	Expression condition;

	Statement ifTrue;
	Statement ifFalse;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(
				condition,
				ifTrue,
				ifFalse
		);
	}
}
