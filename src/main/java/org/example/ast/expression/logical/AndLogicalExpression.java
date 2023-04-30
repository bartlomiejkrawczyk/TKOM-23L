package org.example.ast.expression.logical;

import lombok.Value;
import org.example.ast.expression.LogicalExpression;

@Value
public class AndLogicalExpression implements LogicalExpression {

	LogicalExpression left;
	LogicalExpression right;
}
