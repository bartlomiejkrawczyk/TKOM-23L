package org.example.ast.expression.arithmetic;

import lombok.ToString;
import lombok.Value;
import org.example.ast.expression.ArithmeticExpression;

@ToString(exclude = {"left", "right"})
@Value
public class SubtractArithmeticExpression implements BinaryArithmeticExpression {

	ArithmeticExpression left;
	ArithmeticExpression right;
}
