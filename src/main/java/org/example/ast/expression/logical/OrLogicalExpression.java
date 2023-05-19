package org.example.ast.expression.logical;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.token.Position;

@ToString(exclude = {"left", "right"})
@EqualsAndHashCode(exclude = "position")
@Value
public class OrLogicalExpression implements BinaryLogicalExpression {

	Expression left;
	Expression right;

	Position position;
}
