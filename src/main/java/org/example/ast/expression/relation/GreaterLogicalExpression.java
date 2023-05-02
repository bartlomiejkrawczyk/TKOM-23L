package org.example.ast.expression.relation;

import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.expression.BinaryExpression;
import org.example.ast.expression.LogicalExpression;

@ToString(exclude = {"left", "right"})
@Value
public class GreaterLogicalExpression implements LogicalExpression, BinaryExpression {

	Expression left;
	Expression right;
}
