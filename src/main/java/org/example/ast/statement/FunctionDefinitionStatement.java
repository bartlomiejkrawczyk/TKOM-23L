package org.example.ast.statement;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Node;
import org.example.ast.Statement;
import org.example.ast.expression.Argument;
import org.example.ast.expression.BlockStatement;
import org.example.ast.type.TypeDeclaration;
import org.example.token.Position;
import org.example.visitor.Visitor;

@ToString(exclude = {"body"})
@EqualsAndHashCode(exclude = "position")
@Value
public class FunctionDefinitionStatement implements Statement {

	String name;
	List<Argument> arguments;
	TypeDeclaration returnType;
	BlockStatement body;

	Position position;

	@Override
	public List<Node> getChildrenExpressions() {
		return List.of(
				body
		);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
