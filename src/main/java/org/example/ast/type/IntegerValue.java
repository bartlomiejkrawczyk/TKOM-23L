package org.example.ast.type;

import lombok.EqualsAndHashCode;
import org.example.ast.Value;
import org.example.ast.expression.ArithmeticExpression;
import org.example.token.Position;


@EqualsAndHashCode(exclude = "position")
@lombok.Value
public class IntegerValue implements Value, ArithmeticExpression {

	Integer value;

	Position position;
}
