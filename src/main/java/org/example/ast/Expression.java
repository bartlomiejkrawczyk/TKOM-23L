package org.example.ast;

import org.example.interpreter.Environment;

public interface Expression extends Statement {

	default void evaluate(Environment environment) {

	}
}
