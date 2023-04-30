package org.example.ast.type;

import org.example.ast.Value;
import org.example.ast.expression.LogicalExpression;

@lombok.Value
public class BooleanValue implements Value, LogicalExpression {

	Boolean value;
}
