package org.example.ast.expression.arithmetic;

import lombok.Value;
import org.example.ast.Expression;

@Value
public class AddArithmeticExpression implements BinaryArithmeticExpression {

	Expression left;
	Expression right;
}
