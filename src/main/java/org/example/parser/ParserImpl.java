package org.example.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.ast.expression.DeclarationExpression;
import org.example.ast.expression.FunctionDefinitionExpression;
import org.example.ast.expression.Program;
import org.example.error.ErrorHandler;
import org.example.lexer.Lexer;
import org.example.parser.error.UnexpectedTokenException;
import org.example.token.Token;
import org.example.token.TokenType;

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

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public Program parseProgram() {
		nextToken();
		var functionDefinitions = new HashMap<String, FunctionDefinitionExpression>();
		var declarations = new ArrayList<DeclarationExpression>();

		while (tryParseFunctionDefinition(functionDefinitions)
				|| tryParseDeclaration(declarations)
				|| skipIf(TokenType.SEMICOLON)) {
			// ignore
		}

		if (currentToken.getType() != TokenType.END_OF_FILE) {
			var exception = new UnexpectedTokenException(TokenType.END_OF_FILE, currentToken);
			errorHandler.handleParserException(exception);
		}

		return new Program(functionDefinitions, declarations);
	}

	private boolean skipIf(TokenType tokenType) {
		if (currentToken.getType() != tokenType) {
			return false;
		}
		nextToken();
		return true;
	}

	private void skipUntil(TokenType tokenType) {
		while (currentToken.getType() != tokenType || currentToken.getType() != TokenType.END_OF_FILE) {
			nextToken();
		}
	}

	private boolean tryParseFunctionDefinition(Map<String, FunctionDefinitionExpression> functionDefinitions) {
		if (currentToken.getType() != TokenType.FUNCTION_DEFINITION) {
			return false;
		}
		nextToken();

		if (currentToken.getType() != TokenType.IDENTIFIER) {
			errorHandler.handleParserException(new UnexpectedTokenException(TokenType.IDENTIFIER, currentToken));
			skipUntil(TokenType.SEMICOLON);
			return true;
		}

		var name = currentToken.<String>getValue();

		if (!skipIf(TokenType.OPEN_ROUND_PARENTHESES)) {
			errorHandler.handleParserException(new UnexpectedTokenException(TokenType.OPEN_ROUND_PARENTHESES, currentToken));
			skipUntil(TokenType.SEMICOLON);
		}

		return true;
	}

	private boolean tryParseDeclaration(List<DeclarationExpression> declarations) {
		return false;
	}

}
