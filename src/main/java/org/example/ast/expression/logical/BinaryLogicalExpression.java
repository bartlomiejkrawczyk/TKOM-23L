package org.example.ast.expression.logical;

import org.example.ast.expression.BinaryExpression;
import org.example.ast.expression.LogicalExpression;
import org.example.interpreter.Visitor;

public interface BinaryLogicalExpression extends LogicalExpression, BinaryExpression {

	@Override
	default void accept(Visitor visitor) {
		visitor.visit(this);
	}

	boolean needsFurtherProcessing(boolean first);

	boolean evaluate(boolean first, boolean second);
}
