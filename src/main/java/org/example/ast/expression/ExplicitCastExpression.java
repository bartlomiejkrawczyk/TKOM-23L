package org.example.ast.expression;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.type.TypeDeclaration;
import org.example.token.Position;
import org.example.visitor.Visitor;

@ToString(exclude = {"expression"})
@EqualsAndHashCode(exclude = "position")
@Value
public class ExplicitCastExpression implements Expression {

	TypeDeclaration type;
	Expression expression;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(expression);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
