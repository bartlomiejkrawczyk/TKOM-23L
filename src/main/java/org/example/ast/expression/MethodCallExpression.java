package org.example.ast.expression;

import lombok.Value;
import org.example.ast.Expression;

@Value
public class MethodCallExpression implements ValueExpression {

	Expression object;

	FunctionCallExpression function;
}
