package org.example.ast.expression.arithmetic;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Expression;
import org.example.token.Position;

@ToString(exclude = {"left", "right"})
@EqualsAndHashCode(exclude = "position")
@Value
public class DivideArithmeticExpression implements BinaryArithmeticExpression {

	Expression left;
	Expression right;

	Position position;
}
