package org.example.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.NonNull;
import org.example.ast.Expression;
import org.example.ast.Program;
import org.example.ast.Statement;
import org.example.ast.ValueType;
import org.example.ast.expression.Argument;
import org.example.ast.expression.ArithmeticExpression;
import org.example.ast.expression.BlockExpression;
import org.example.ast.expression.FunctionCallExpression;
import org.example.ast.expression.IdentifierExpression;
import org.example.ast.expression.LogicalExpression;
import org.example.ast.expression.MapExpression;
import org.example.ast.expression.MethodCallExpression;
import org.example.ast.expression.SelectExpression;
import org.example.ast.expression.TupleCallExpression;
import org.example.ast.expression.TupleExpression;
import org.example.ast.statement.AssignmentStatement;
import org.example.ast.statement.DeclarationStatement;
import org.example.ast.statement.ForStatement;
import org.example.ast.statement.FunctionDefinitionStatement;
import org.example.ast.statement.IfStatement;
import org.example.ast.statement.WhileStatement;
import org.example.ast.type.TypeDeclaration;
import org.example.error.ErrorHandler;
import org.example.lexer.Lexer;
import org.example.parser.error.CriticalParserException;
import org.example.parser.error.ExpectedTypeDeclarationException;
import org.example.parser.error.UnexpectedTokenException;
import org.example.token.Token;
import org.example.token.TokenType;

public class ParserImpl implements Parser {

	private final List<Supplier<Optional<? extends Statement>>> statementSuppliers = List.of(
			this::parseIfStatement,
			this::parseWhileStatement,
			this::parseForStatement,
			this::parseDeclarationStatement,
			this::parseAssignmentStatement,
			this::parseSingleExpression,
			this::parseBlock
	);

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
		var functionDefinitions = new HashMap<String, FunctionDefinitionStatement>();
		var declarations = new ArrayList<DeclarationStatement>();

		boolean parse;
		do {
			parse = fillIn(this::parseFunctionDefinition, it -> functionDefinitions.put(it.getName(), it))
					.orElseGet(() -> fillIn(this::parseDeclarationStatement, declarations::add)
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
		while (currentToken.getType() != tokenType && currentToken.getType() != TokenType.END_OF_FILE) {
			nextToken();
		}
	}

	private void handleSkip(TokenType expected) {
		if (!skipIf(expected)) {
			errorHandler.handleParserException(new UnexpectedTokenException(expected, currentToken));
			skipUntil(expected);
		}
	}

	@NonNull
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private <T> T retrieveItem(@NonNull Optional<T> optional, String errorMessage) {
		if (optional.isEmpty()) {
			throw new CriticalParserException(errorMessage, currentToken);
		}
		return optional.get();
	}

	private Optional<FunctionDefinitionStatement> parseFunctionDefinition() {
		if (currentToken.getType() != TokenType.FUNCTION_DEFINITION) {
			return Optional.empty();
		}
		nextToken();
		var name = getIdentifierOrThrow();
		nextToken();
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
		return Optional.of(new FunctionDefinitionStatement(name, arguments, returnType, block));
	}

	private Optional<DeclarationStatement> parseDeclarationStatement() {
		var typeDeclaration = parseTypeDeclaration();
		if (typeDeclaration.isEmpty()) {
			return Optional.empty();
		}
		var identifier = getIdentifierOrThrow();
		// TODO: may add default values for all the object types (:
		handleSkip(TokenType.EQUALS);
		var expression = retrieveItem(parseExpression(), "Missing expression");
		handleSkip(TokenType.SEMICOLON);
		return Optional.of(
				new DeclarationStatement(new Argument(identifier, typeDeclaration.get()), expression)
		);
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
		if (!skipIf(TokenType.OPEN_CURLY_PARENTHESES)) {
			return Optional.empty();
		}
		var statements = new ArrayList<Statement>();
		var statement = parseStatement();
		while (statement.isPresent()) {
			statements.add(statement.get());
			statement = parseStatement();
		}
		handleSkip(TokenType.CLOSED_CURLY_PARENTHESES);
		return Optional.of(new BlockExpression(statements));
	}

	@SuppressWarnings("unchecked")
	private Optional<Statement> parseStatement() {
		for (var supplier : statementSuppliers) {
			var statement = supplier.get();
			if (statement.isPresent()) {
				return (Optional<Statement>) statement;
			}
		}
		// TODO: check for infinite loops :^o
		if (skipIf(TokenType.SEMICOLON)) {
			return Optional.of(new BlockExpression(List.of()));
		}
		return Optional.empty();
	}

	private Optional<IfStatement> parseIfStatement() {
		if (!skipIf(TokenType.IF)) {
			return Optional.empty();
		}
		var condition = retrieveItem(parseLogicalExpression(), "Missing condition");
		var ifTrue = retrieveItem(parseStatement(), "Missing statement");
		Optional<Statement> ifFalse = skipIf(TokenType.ELSE)
				? parseStatement()
				: Optional.empty();

		return Optional.of(
				new IfStatement(condition, ifTrue, ifFalse.orElseGet(() -> new BlockExpression(List.of())))
		);
	}

	private Optional<WhileStatement> parseWhileStatement() {
		if (!skipIf(TokenType.WHILE)) {
			return Optional.empty();
		}
		var condition = retrieveItem(parseLogicalExpression(), "Missing logical expression");
		var statement = parseStatement().orElseGet(() -> new BlockExpression(List.of()));
		return Optional.of(
				new WhileStatement(condition, statement)
		);
	}

	private Optional<ForStatement> parseForStatement() {
		if (!skipIf(TokenType.FOR)) {
			return Optional.empty();
		}

		handleSkip(TokenType.OPEN_ROUND_PARENTHESES);
		var type = retrieveItem(parseTypeDeclaration(), "Missing type declaration");
		var identifier = getIdentifierOrThrow();
		nextToken();
		handleSkip(TokenType.COLON);

		var iterable = retrieveItem(parseExpression(), "Missing iterable expression");

		nextToken();
		handleSkip(TokenType.CLOSED_ROUND_PARENTHESES);

		var body = parseStatement().orElseGet(() -> new BlockExpression(List.of()));

		return Optional.of(new ForStatement(new Argument(identifier, type), iterable, body));
	}

	private Optional<AssignmentStatement> parseAssignmentStatement() {
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			return Optional.empty();
		}
		var identifier = currentToken.<String>getValue();
		nextToken();
		handleSkip(TokenType.EQUALS);
		var expression = retrieveItem(parseExpression(), "Assignment should end with an expression");
		handleSkip(TokenType.SEMICOLON);
		return Optional.of(
				new AssignmentStatement(
						identifier,
						expression
				)
		);
	}

	private Optional<Expression> parseSingleExpression() {
		var expression = parseExpression();
		if (expression.isPresent()) {
			handleSkip(TokenType.SEMICOLON);
		}
		return expression;
	}

	// TODO: extract to single function starting with identifier !!!
	private final List<Supplier<Optional<? extends Expression>>> expressionSuppliers = List.of(
			this::parseIdentifier,
			this::parseArithmeticExpression,
			this::parseLogicalExpression,
			this::parseFunctionCall,
			this::parseMethodCall,
			this::parseTupleCall,
			this::parseSelectExpression,
			this::parseTupleExpression,
			this::parseMapExpression
	);

	@SuppressWarnings("unchecked")
	private Optional<Expression> parseExpression() {
		for (var supplier : expressionSuppliers) {
			var expression = supplier.get();
			if (expression.isPresent()) {
				return (Optional<Expression>) expression;
			}
		}
		return Optional.empty();
	}

	private Optional<IdentifierExpression> parseIdentifier() {
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			return Optional.empty();
		}
		return Optional.of(new IdentifierExpression(currentToken.getValue()));
	}

	private Optional<ArithmeticExpression> parseArithmeticExpression() {
		// TODO: implement me!
		return Optional.empty();
	}

	private Optional<LogicalExpression> parseLogicalExpression() {
		// TODO: implement me!
		return Optional.empty();
	}

	private Optional<FunctionCallExpression> parseFunctionCall() {
		// TODO: implement me!
		return Optional.empty();
	}

	private Optional<MethodCallExpression> parseMethodCall() {
		// TODO: implement me!
		return Optional.empty();
	}

	private Optional<TupleCallExpression> parseTupleCall() {
		// TODO: implement me!
		return Optional.empty();
	}

	private Optional<SelectExpression> parseSelectExpression() {
		// TODO: implement me!
		return Optional.empty();
	}

	private Optional<TupleExpression> parseTupleExpression() {
		// TODO: implement me!
		return Optional.empty();
	}

	private Optional<MapExpression> parseMapExpression() {
		// TODO: implement me!
		return Optional.empty();
	}

	private String getIdentifierOrThrow() {
		return retrieveItem(
				Optional.of(currentToken).filter(it -> it.getType() == TokenType.IDENTIFIER),
				"Missing identifier"
		).getValue();
	}
}
