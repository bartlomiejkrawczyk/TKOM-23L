package org.example.ast.expression.arithmetic;

import lombok.Value;
import org.example.ast.expression.ArithmeticExpression;

@Value
public class MultiplyArithmeticExpression implements BinaryArithmeticExpression {

	ArithmeticExpression left;
	ArithmeticExpression right;
}
