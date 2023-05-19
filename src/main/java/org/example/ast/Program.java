package org.example.ast;

import java.util.Map;
import java.util.stream.Stream;
import lombok.ToString;
import lombok.Value;
import org.example.ast.statement.DeclarationStatement;
import org.example.ast.statement.FunctionDefinitionStatement;
import org.example.interpreter.Visitor;
import org.example.token.Position;

@ToString(exclude = {"functionDefinitions", "declarations"})
@Value
public class Program implements Node {

	Map<String, FunctionDefinitionStatement> functionDefinitions;
	Map<String, DeclarationStatement> declarations;

	@Override
	public Position getPosition() {
		return Position.builder()
				.characterNumber(1)
				.line(1)
				.build();
	}

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return Stream.concat(
						functionDefinitions.values().stream(),
						declarations.values().stream()
				)
				.map(Node.class::cast)
				.toList();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
