package org.example.ast;

import java.util.List;
import org.example.visitor.Visitor;

public interface Node {

	default Iterable<Node> getChildrenExpressions() {
		return List.of();
	}

	default void visit(Visitor visitor) {
		visitor.accept(this);
	}
}
