package org.example.ast.statement;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Statement;

@Value
public class IfStatement implements Statement {

	Expression condition;

	Statement ifTrue;
	Statement ifFalse;
}
