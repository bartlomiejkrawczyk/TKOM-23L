package org.example.ast.expression;

import java.util.Map;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.type.TupleElement;

@Value
public class TupleExpression implements Expression {

	Map<String, Expression> elements;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return elements.entrySet()
				.stream()
				.map(entry -> new TupleElement(entry.getKey(), entry.getValue()))
				.map(Node.class::cast)
				.toList();
	}
}
