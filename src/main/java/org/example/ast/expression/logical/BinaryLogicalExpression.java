package org.example.ast.expression.logical;

import org.example.ast.expression.BinaryExpression;
import org.example.ast.expression.LogicalExpression;
import org.example.visitor.Visitor;

public interface BinaryLogicalExpression extends LogicalExpression, BinaryExpression {

	@Override
	default void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
