package org.example.ast.type;

import org.example.ast.Value;
import org.example.ast.statement.FunctionDefinitionStatement;

@lombok.Value
public class ComparatorValue implements Value {

	FunctionDefinitionStatement value;
}
