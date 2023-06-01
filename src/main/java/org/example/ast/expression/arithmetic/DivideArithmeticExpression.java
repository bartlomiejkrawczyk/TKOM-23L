package org.example.ast.expression.arithmetic;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.interpreter.error.ZeroDivisionException;
import org.example.token.Position;

@ToString(exclude = {"left", "right"})
@EqualsAndHashCode(exclude = "position")
@Value
public class DivideArithmeticExpression implements BinaryArithmeticExpression {

	Expression left;
	Expression right;

	Position position;

	@Override
	public int evaluate(int first, int second) {
		if (second == 0) {
			throw new ZeroDivisionException();
		}
		return first / second;
	}

	@Override
	public double evaluate(double first, double second) {
		if (second == 0.0) {
			throw new ZeroDivisionException();
		}
		return first / second;
	}
}
