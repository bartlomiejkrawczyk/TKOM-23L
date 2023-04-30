package org.example.ast.expression;

import java.util.Map;
import lombok.Value;
import org.example.ast.Expression;

@Value
public class MapExpression implements Expression {

	Map<Expression, Expression> elements;
}
