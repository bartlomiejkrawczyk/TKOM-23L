package org.example.ast.statement;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.Statement;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@ToString(exclude = {"condition", "ifTrue", "ifFalse"})
@EqualsAndHashCode(exclude = "position")
@Value
public class IfStatement implements Statement {

	Expression condition;

	Statement ifTrue;
	Statement ifFalse;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(
				condition,
				ifTrue,
				ifFalse
		);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
