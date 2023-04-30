package org.example.ast.expression;

import lombok.Value;
import org.example.ast.Expression;

@Value
public class IdentifierExpression implements Expression, ArithmeticExpression, LogicalExpression {

	String name;
}
