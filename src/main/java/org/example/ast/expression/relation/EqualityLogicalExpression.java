package org.example.ast.expression.relation;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.example.ast.Expression;
import org.example.token.Position;

@ToString(exclude = {"left", "right"})
@EqualsAndHashCode(exclude = "position")
@Value
public class EqualityLogicalExpression implements EqualityRelationLogicalExpression {

	Expression left;
	Expression right;

	Position position;

	@Override
	public boolean evaluate(int first, int second) {
		return first == second;
	}

	@Override
	public boolean evaluate(double first, double second) {
		return first == second;
	}

	@Override
	public boolean evaluate(String first, String second) {
		return StringUtils.equals(first, second);
	}
}
