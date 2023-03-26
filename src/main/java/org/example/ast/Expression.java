package org.example.ast;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.example.lexer.CharactersUtility;


public interface Expression {

	ExpressionType getType();

	default Iterable<Expression> getChildrenExpressions() {
		return List.of();
	}

	default String print() {
		var buffer = new StringBuilder();
		print(buffer, StringUtils.EMPTY, StringUtils.EMPTY);
		return buffer.toString();
	}

	default void print(StringBuilder buffer, String prefix, String childrenPrefix) {
		buffer.append(prefix);
		buffer.append(this);
		buffer.append(CharactersUtility.NEW_LINE);
		var children = getChildrenExpressions().iterator();
		while (children.hasNext()) {
			var expression = children.next();
			if (children.hasNext()) {
				expression.print(buffer, childrenPrefix + "|--- ", childrenPrefix + "|    ");
			} else {
				expression.print(buffer, childrenPrefix + "\\--- ", childrenPrefix + "     ");
			}
		}
	}
}
