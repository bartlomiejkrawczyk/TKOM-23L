package org.example.ast.expression;

import java.util.Map;
import lombok.Value;
import org.example.ast.Expression;

@Value
public class TupleExpression implements Expression {

	Map<String, Expression> elements;
}
