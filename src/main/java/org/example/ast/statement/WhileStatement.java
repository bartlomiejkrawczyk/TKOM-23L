package org.example.ast.statement;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Statement;

@Value
public class WhileStatement implements Statement {

	Expression condition;

	Statement body;
}
