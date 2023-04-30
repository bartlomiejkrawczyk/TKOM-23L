package org.example.ast.statement;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Statement;
import org.example.ast.expression.Argument;

@Value
public class ForStatement implements Statement {

	Argument argument;
	Expression iterable;

	Statement body;
}
