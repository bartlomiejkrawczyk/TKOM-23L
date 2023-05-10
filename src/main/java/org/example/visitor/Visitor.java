package org.example.visitor;

import org.example.ast.Node;

public interface Visitor {

	<T extends Node> void accept(T node);
}
