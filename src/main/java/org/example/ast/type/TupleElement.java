package org.example.ast.type;

import java.util.List;
import lombok.ToString;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.Value;

@ToString(exclude = {"value"})
@lombok.Value
public class TupleElement implements Value {

	String name;
	Expression value;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return List.of(
				value
		);
	}
}
