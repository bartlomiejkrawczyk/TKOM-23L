package org.example.ast.expression;

import java.util.Map;
import java.util.stream.Stream;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;

@ToString(exclude = {"elements"})
@Value
public class MapExpression implements Expression {

	Map<Expression, Expression> elements;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return elements.entrySet()
				.stream()
				.flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
				.map(Node.class::cast)
				.toList();
	}
}
