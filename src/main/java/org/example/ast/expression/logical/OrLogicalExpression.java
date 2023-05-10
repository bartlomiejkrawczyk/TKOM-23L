package org.example.ast.expression.logical;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.expression.BinaryExpression;
import org.example.ast.expression.LogicalExpression;
import org.example.token.Position;

@ToString(exclude = {"left", "right"})
@EqualsAndHashCode(exclude = "position")
@Value
public class OrLogicalExpression implements LogicalExpression, BinaryExpression {

	Expression left;
	Expression right;

	Position position;
}
