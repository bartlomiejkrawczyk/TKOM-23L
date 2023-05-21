package org.example.ast.expression.logical;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.token.Position;

@ToString(exclude = {"left", "right"})
@EqualsAndHashCode(exclude = "position")
@Value
public class AndLogicalExpression implements BinaryLogicalExpression {

	Expression left;
	Expression right;

	Position position;

	@Override
	public boolean needsFurtherProcessing(boolean first) {
		return first;
	}

	@Override
	public boolean evaluate(boolean first, boolean second) {
		return first && second;
	}
}
