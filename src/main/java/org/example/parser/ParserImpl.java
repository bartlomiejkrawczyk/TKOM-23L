package org.example.parser;

import java.util.ArrayList;
import org.example.ast.expression.FunctionExpression;
import org.example.ast.expression.Program;
import org.example.error.ErrorHandler;
import org.example.lexer.Lexer;
import org.example.token.Token;

public class ParserImpl implements Parser {

	private final Lexer lexer;
	private final ErrorHandler errorHandler;

	private Token currentToken;

	public ParserImpl(Lexer lexer, ErrorHandler errorHandler) {
		this.lexer = lexer;
		this.errorHandler = errorHandler;
	}

	private Token nextToken() {
		currentToken = lexer.nextToken();
		return currentToken;
	}

	@Override
	public Program parseProgram() {
		var functions = new ArrayList<FunctionExpression>();
		nextToken();


		return new Program(functions);
	}

}
