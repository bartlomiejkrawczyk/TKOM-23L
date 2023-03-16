package org.example.parser;

import org.example.ast.Expression;

public interface Parser {

	Expression nextExpression();
}
