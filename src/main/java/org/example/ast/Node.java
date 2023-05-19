package org.example.ast;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.example.interpreter.Visitor;
import org.example.lexer.CharactersUtility;
import org.example.token.Position;

public interface Node {

	Position getPosition();

	default Iterable<Node> getChildrenExpressions() {
		return List.of();
	}

	void accept(Visitor visitor);

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
				expression.print(buffer, childrenPrefix + "`--- ", childrenPrefix + "     ");
			}
		}
	}
}
