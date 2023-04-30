package org.example.ast.expression.logical;

import lombok.Value;
import org.example.ast.expression.LogicalExpression;

@Value
public class OrLogicalExpression implements LogicalExpression {

	LogicalExpression left;
	LogicalExpression right;
}
