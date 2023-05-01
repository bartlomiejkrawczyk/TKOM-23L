package org.example.ast.statement;

import lombok.Value;
import org.example.ast.Expression;
import org.example.ast.Statement;

@Value
public class ReturnStatement implements Statement {

	Expression expression;
}
