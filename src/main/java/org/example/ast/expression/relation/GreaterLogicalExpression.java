package org.example.ast.expression.relation;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.token.Position;

@ToString(exclude = {"left", "right"})
@EqualsAndHashCode(exclude = "position")
@Value
public class GreaterLogicalExpression implements RelationLogicalExpression {

	Expression left;
	Expression right;

	Position position;

	@Override
	public boolean evaluate(int first, int second) {
		return first > second;
	}

	@Override
	public boolean evaluate(double first, double second) {
		return first > second;
	}
}
