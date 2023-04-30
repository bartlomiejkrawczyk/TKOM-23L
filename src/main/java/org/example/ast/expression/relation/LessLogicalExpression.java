package org.example.ast.expression.relation;

import lombok.Value;
import org.example.ast.expression.ArithmeticExpression;
import org.example.ast.expression.LogicalExpression;

@Value
public class LessLogicalExpression implements LogicalExpression {

	ArithmeticExpression left;
	ArithmeticExpression right;
}
