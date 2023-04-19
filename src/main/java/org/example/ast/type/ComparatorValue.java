package org.example.ast.type;

import org.example.ast.Value;
import org.example.ast.expression.FunctionDefinitionExpression;

@lombok.Value
public class ComparatorValue implements Value {

	FunctionDefinitionExpression value;
}
