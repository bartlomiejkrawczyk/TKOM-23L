package org.example.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.example.ast.ValueType;
import org.example.ast.expression.Argument;
import org.example.ast.expression.BlockExpression;
import org.example.ast.expression.DeclarationExpression;
import org.example.ast.expression.FunctionDefinitionExpression;
import org.example.ast.expression.Program;
import org.example.ast.type.TypeDeclaration;
import org.example.error.ErrorHandler;
import org.example.lexer.Lexer;
import org.example.parser.error.ExpectedTypeDeclarationException;
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

	@Override
	public Program parseProgram() {
		nextToken();
		var functionDefinitions = new HashMap<String, FunctionDefinitionExpression>();
		var declarations = new ArrayList<DeclarationExpression>();

		boolean parse;
		do {
			parse = fillIn(this::parseFunctionDefinition, it -> functionDefinitions.put(it.getName(), it))
					.orElseGet(() -> fillIn(this::parseDeclaration, declarations::add)
							.orElseGet(() -> skipIf(TokenType.SEMICOLON)));
		} while (parse);

		if (currentToken.getType() != TokenType.END_OF_FILE) {
			var exception = new UnexpectedTokenException(TokenType.END_OF_FILE, currentToken);
			errorHandler.handleParserException(exception);
		}

		return new Program(functionDefinitions, declarations);
	}

	private <T> Optional<Boolean> fillIn(Supplier<Optional<T>> supplier, Consumer<T> consumer) {
		return supplier.get()
				.map(it -> {
					consumer.accept(it);
					return true;
				});
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

	private void handleSkip(TokenType expected) {
		if (!skipIf(expected)) {
			errorHandler.handleParserException(new UnexpectedTokenException(expected, currentToken));
			skipUntil(expected);
		}
	}

	private Optional<FunctionDefinitionExpression> parseFunctionDefinition() {
		if (currentToken.getType() != TokenType.FUNCTION_DEFINITION) {
			return Optional.empty();
		}
		nextToken();

		if (currentToken.getType() != TokenType.IDENTIFIER) {
			errorHandler.handleParserException(new UnexpectedTokenException(TokenType.IDENTIFIER, currentToken));
			skipUntil(TokenType.CLOSED_CURLY_PARENTHESES);
			// TODO: reconsider
			return Optional.empty();
		}

		var name = currentToken.<String>getValue();
		handleSkip(TokenType.OPEN_ROUND_PARENTHESES);

		var arguments = new ArrayList<Argument>();
		var argument = parseArgument();
		while (argument.isPresent()) {
			arguments.add(argument.get());
			argument = parseArgument();
		}

		handleSkip(TokenType.CLOSED_ROUND_PARENTHESES);

		TypeDeclaration returnType;
		if (skipIf(TokenType.COLON)) {
			returnType = parseTypeDeclaration()
					.orElseGet(() -> {
						errorHandler.handleParserException(new ExpectedTypeDeclarationException(currentToken));
						return new TypeDeclaration(ValueType.VOID);
					});
		} else {
			returnType = new TypeDeclaration(ValueType.VOID);
		}

		var block = parseBlock().orElseGet(() -> new BlockExpression(List.of()));
		return Optional.of(new FunctionDefinitionExpression(name, arguments, returnType, block));
	}

	private Optional<DeclarationExpression> parseDeclaration() {
		// TODO: parse declaration
		return Optional.empty();
	}

	private Optional<Argument> parseArgument() {
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			return Optional.empty();
		}
		var identifier = currentToken.<String>getValue();
		handleSkip(TokenType.COLON);
		var type = parseTypeDeclaration().orElseGet(() -> new TypeDeclaration(ValueType.INTEGER));
		return Optional.of(new Argument(identifier, type));
	}

	private Optional<TypeDeclaration> parseTypeDeclaration() {
		var valueType = ValueType.of(currentToken.getType());

		if (valueType.isEmpty()) {
			return Optional.empty();
		} else {
			nextToken();
		}

		var type = valueType.get();
		var types = new ArrayList<TypeDeclaration>();
		if (type.isComplex()) {
			handleSkip(TokenType.LESS);
			var declaration = parseTypeDeclaration();
			while (declaration.isPresent()) {
				types.add(declaration.get());
				if (!skipIf(TokenType.COMMA)) {
					break;
				}
				declaration = parseTypeDeclaration();
			}
			handleSkip(TokenType.GREATER);
		}
		return Optional.of(new TypeDeclaration(type, types));
	}

	private Optional<BlockExpression> parseBlock() {
		// TODO: implement me!
		return Optional.empty();
	}
}
