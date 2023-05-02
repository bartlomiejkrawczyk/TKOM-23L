package org.example.ast.expression.logical;

import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.expression.BinaryExpression;
import org.example.ast.expression.LogicalExpression;

@ToString(exclude = {"left", "right"})
@Value
public class OrLogicalExpression implements LogicalExpression, BinaryExpression {

	Expression left;
	Expression right;
}
