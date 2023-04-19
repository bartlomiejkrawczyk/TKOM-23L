package org.example.ast;

import org.example.interpreter.Environment;

public interface Expression extends Statement {

	default ExpressionType getType() {
		return ExpressionType.UNKNOWN;
	}

	default void evaluate(Environment environment) {

	}
}
