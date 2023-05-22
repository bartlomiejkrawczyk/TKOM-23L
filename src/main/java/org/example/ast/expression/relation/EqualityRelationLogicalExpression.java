package org.example.ast.expression.relation;

import org.example.ast.expression.BinaryExpression;
import org.example.ast.expression.LogicalExpression;
import org.example.interpreter.Visitor;

public interface EqualityRelationLogicalExpression extends LogicalExpression, BinaryExpression {

	@Override
	default void accept(Visitor visitor) {
		visitor.visit(this);
	}

	boolean evaluate(int first, int second);

	boolean evaluate(double first, double second);

	boolean evaluate(String first, String second);
}
