package org.example.ast.expression;

import io.vavr.Tuple3;
import java.util.List;
import java.util.Map;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.example.ast.Expression;

@Value
public class SelectExpression implements Expression {

	TupleExpression select;
	Map.Entry<String, Expression> from;
	List<Tuple3<String, Expression, Expression>> join;
	Expression where;
	List<Expression> groupBy;
	Expression having;
	List<Pair<Expression, Boolean>> orderBy;
}
