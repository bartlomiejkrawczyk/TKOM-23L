package org.example.ast.expression.arithmetic;

import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;

@ToString(exclude = {"left", "right"})
@Value
public class DivideArithmeticExpression implements BinaryArithmeticExpression {

	Expression left;
	Expression right;
}
