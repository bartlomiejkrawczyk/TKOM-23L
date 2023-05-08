package org.example.ast.expression;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.type.TypeDeclaration;

@ToString(exclude = {"expression"})
@Value
public class ExplicitCastExpression implements Expression {

	TypeDeclaration type;
	Expression expression;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(expression);
	}
}
