package org.example.ast.statement;

import java.util.List;
import lombok.ToString;
import lombok.Value;
import org.example.ast.Node;
import org.example.ast.Statement;
import org.example.ast.expression.Argument;
import org.example.ast.expression.BlockExpression;
import org.example.ast.type.TypeDeclaration;


@ToString(exclude = {"body"})
@Value
public class FunctionDefinitionStatement implements Statement {

	String name;
	// TODO: Consider instead of ValueType using String to allow for user defined types
	List<Argument> arguments;
	TypeDeclaration returnType;
	BlockExpression body;

	@Override
	public List<Node> getChildrenExpressions() {
		return List.of(
				body
		);
	}
}
