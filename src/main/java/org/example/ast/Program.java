package org.example.ast;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.ToString;
import lombok.Value;
import org.example.ast.statement.DeclarationStatement;
import org.example.ast.statement.FunctionDefinitionStatement;

@ToString(exclude = {"functionDefinitions", "declarations"})
@Value
public class Program implements Node {

	Map<String, FunctionDefinitionStatement> functionDefinitions;
	List<DeclarationStatement> declarations;

	@Override
	public Iterable<Node> getChildrenExpressions() {
		return Stream.concat(
						functionDefinitions.values().stream(),
						declarations.stream()
				)
				.map(Node.class::cast)
				.toList();
	}
}
