package org.example.visitor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.example.ast.Node;
import org.example.lexer.CharactersUtility;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrintingVisitor implements Visitor {

	final StringBuilder buffer = new StringBuilder();
	String prefix = StringUtils.EMPTY;
	String childrenPrefix = StringUtils.EMPTY;

	@Override
	public <T extends Node> void visit(T node) {
		var previousPrefix = prefix;
		var previousChildrenPrefix = childrenPrefix;

		buffer.append(prefix);
		buffer.append(node.toString());
		buffer.append(CharactersUtility.NEW_LINE);

		var children = node.getChildrenExpressions().iterator();
		while (children.hasNext()) {
			var expression = children.next();
			if (children.hasNext()) {
				prefix = previousChildrenPrefix + "|--- ";
				childrenPrefix = previousChildrenPrefix + "|    ";
				expression.accept(this);
			} else {
				prefix = previousChildrenPrefix + "`--- ";
				childrenPrefix = previousChildrenPrefix + "     ";
				expression.accept(this);
			}
		}

		prefix = previousPrefix;
		childrenPrefix = previousChildrenPrefix;
	}

	public String print() {
		return buffer.toString();
	}
}
