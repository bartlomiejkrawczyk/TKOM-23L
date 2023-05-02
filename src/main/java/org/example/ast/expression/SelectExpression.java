package org.example.ast.expression;

import io.vavr.Tuple3;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.ToString;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.type.TupleElement;

@ToString(exclude = {"select", "from", "join", "where", "having", "orderBy"})
@Value
public class SelectExpression implements Expression {

	TupleExpression select;
	Map.Entry<String, Expression> from;
	List<Tuple3<String, Expression, Expression>> join;
	Expression where;
	List<Expression> groupBy;
	Expression having;
	List<Pair<Expression, Boolean>> orderBy;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return Stream.of(
						Stream.of(select),
						Stream.of(new TupleElement(from.getKey(), from.getValue())),
						join.stream().flatMap(tuple -> Stream.of(new TupleElement(tuple._1, tuple._2), tuple._3)),
						Stream.of(where),
						groupBy.stream(),
						Stream.of(having),
						orderBy.stream().map(Pair::getKey)
				)
				.reduce(Stream::concat)
				.orElseGet(Stream::empty)
				.map(Node.class::cast)
				.toList();
	}
}
