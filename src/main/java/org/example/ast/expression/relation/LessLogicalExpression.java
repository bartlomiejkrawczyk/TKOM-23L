package org.example.ast.expression.relation;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.expression.LogicalExpression;

@Value
public class LessLogicalExpression implements LogicalExpression {

	Expression left;
	Expression right;
}
