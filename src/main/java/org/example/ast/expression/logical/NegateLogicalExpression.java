package org.example.ast.expression.logical;

import lombok.Value;
import org.example.ast.expression.LogicalExpression;

@Value
public class NegateLogicalExpression implements LogicalExpression {

	LogicalExpression expression;
}
