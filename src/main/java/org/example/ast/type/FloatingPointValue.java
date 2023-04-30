package org.example.ast.type;

import org.example.ast.Value;
import org.example.ast.expression.ArithmeticExpression;

@lombok.Value
public class FloatingPointValue implements Value, ArithmeticExpression {

	Double value;
}
