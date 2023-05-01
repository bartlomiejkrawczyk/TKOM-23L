package org.example.ast.expression;

import lombok.Value;
import org.example.ast.Expression;

@Value
public class TupleCallExpression implements ValueExpression {

	Expression object;
	String identifier;
}
