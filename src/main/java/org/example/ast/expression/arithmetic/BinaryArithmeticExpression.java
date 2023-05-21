package org.example.ast.expression.arithmetic;

import org.example.ast.expression.ArithmeticExpression;
import org.example.ast.expression.BinaryExpression;
import org.example.interpreter.Visitor;

public interface BinaryArithmeticExpression extends ArithmeticExpression, BinaryExpression {

	@Override
	default void accept(Visitor visitor) {
		visitor.visit(this);
	}

	int evaluate(int first, int second);

	double evaluate(double first, double second);
}
