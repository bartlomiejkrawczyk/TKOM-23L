package org.example.ast;

import org.example.interpreter.Environment;

public interface Expression extends Node {

	ExpressionType getType();

	default void evaluate(Environment environment) {

	}
}
