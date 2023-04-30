package org.example.ast;

import java.util.List;
import java.util.Map;
import lombok.Value;
import org.example.ast.statement.DeclarationStatement;
import org.example.ast.statement.FunctionDefinitionStatement;

@Value
public class Program implements Node {

	Map<String, FunctionDefinitionStatement> functionDefinitions;
	List<DeclarationStatement> declarations;
}
