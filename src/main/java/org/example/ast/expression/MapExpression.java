package org.example.ast.expression;

import java.util.Map;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@ToString(exclude = {"elements"})
@EqualsAndHashCode(exclude = "position")
@Value
public class MapExpression implements Expression {

	Map<Expression, Expression> elements;

	Position position;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return elements.entrySet()
				.stream()
				.flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
				.map(Node.class::cast)
				.toList();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
