package org.example.ast.type;

import lombok.EqualsAndHashCode;
import org.example.ast.Value;
import org.example.ast.expression.LogicalExpression;
import org.example.token.Position;

@EqualsAndHashCode(exclude = "position")
@lombok.Value
public class BooleanValue implements Value, LogicalExpression {

	Boolean value;

	Position position;
}
