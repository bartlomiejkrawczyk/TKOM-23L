package org.example.ast.expression.relation;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.token.Position;

@ToString(exclude = {"left", "right"})
@EqualsAndHashCode(exclude = "position")
@Value
public class LessLogicalExpression implements RelationLogicalExpression {

	Expression left;
	Expression right;

	Position position;
}
