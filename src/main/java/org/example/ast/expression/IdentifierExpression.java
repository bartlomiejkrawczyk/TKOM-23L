package org.example.ast.expression;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.example.token.Position;

@EqualsAndHashCode(exclude = "position")
@Value
public class IdentifierExpression implements ValueExpression {

	String name;

	Position position;
}
