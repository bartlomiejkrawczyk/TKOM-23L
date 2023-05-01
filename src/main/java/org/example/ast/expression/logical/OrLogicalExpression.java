package org.example.ast.expression.logical;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.expression.LogicalExpression;

@Value
public class OrLogicalExpression implements LogicalExpression {

	Expression left;
	Expression right;
}
