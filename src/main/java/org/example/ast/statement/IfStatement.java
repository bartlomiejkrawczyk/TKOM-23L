package org.example.ast.statement;

import lombok.Value;
import org.example.ast.Statement;
import org.example.ast.expression.LogicalExpression;

@Value
public class IfStatement implements Statement {

	LogicalExpression condition;

	Statement ifTrue;
	Statement ifFalse;
}
