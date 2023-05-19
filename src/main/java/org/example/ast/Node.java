package org.example.ast;

import java.util.List;
import org.example.token.Position;
import org.example.visitor.Visitor;

public interface Node {

	Position getPosition();

	default Iterable<Node> getChildrenExpressions() {
		return List.of();
	}

	void accept(Visitor visitor);
}
