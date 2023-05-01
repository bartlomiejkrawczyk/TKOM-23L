package org.example.ast.expression;

import lombok.Value;

@Value
public class IdentifierExpression implements ValueExpression {

	String name;
}
