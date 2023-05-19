package org.example.parser;

import io.vavr.Function3;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.example.ast.Expression;
import org.example.ast.Program;
import org.example.ast.Statement;
import org.example.ast.ValueType;
import org.example.ast.expression.Argument;
import org.example.ast.expression.BlockStatement;
import org.example.ast.expression.ExplicitCastExpression;
import org.example.ast.expression.FunctionCallExpression;
import org.example.ast.expression.IdentifierExpression;
import org.example.ast.expression.MapExpression;
import org.example.ast.expression.MethodCallExpression;
import org.example.ast.expression.SelectExpression;
import org.example.ast.expression.TupleCallExpression;
import org.example.ast.expression.TupleExpression;
import org.example.ast.expression.arithmetic.AddArithmeticExpression;
import org.example.ast.expression.arithmetic.DivideArithmeticExpression;
import org.example.ast.expression.arithmetic.MultiplyArithmeticExpression;
import org.example.ast.expression.arithmetic.NegationArithmeticExpression;
import org.example.ast.expression.arithmetic.SubtractArithmeticExpression;
import org.example.ast.expression.logical.AndLogicalExpression;
import org.example.ast.expression.logical.NegateLogicalExpression;
import org.example.ast.expression.logical.OrLogicalExpression;
import org.example.ast.expression.relation.EqualityLogicalExpression;
import org.example.ast.expression.relation.GreaterEqualLogicalExpression;
import org.example.ast.expression.relation.GreaterLogicalExpression;
import org.example.ast.expression.relation.InequalityLogicalExpression;
import org.example.ast.expression.relation.LessEqualLogicalExpression;
import org.example.ast.expression.relation.LessLogicalExpression;
import org.example.ast.statement.AssignmentStatement;
import org.example.ast.statement.DeclarationStatement;
import org.example.ast.statement.ForStatement;
import org.example.ast.statement.FunctionDefinitionStatement;
import org.example.ast.statement.IfStatement;
import org.example.ast.statement.ReturnStatement;
import org.example.ast.statement.WhileStatement;
import org.example.ast.type.BooleanValue;
import org.example.ast.type.FloatingPointValue;
import org.example.ast.type.IntegerValue;
import org.example.ast.type.StringValue;
import org.example.ast.type.TypeDeclaration;
import org.example.error.ErrorHandler;
import org.example.lexer.Lexer;
import org.example.lexer.LexerUtility;
import org.example.parser.error.CannotAssignValueToExpressionException;
import org.example.parser.error.CriticalParserException;
import org.example.parser.error.DuplicateDeclaration;
import org.example.parser.error.DuplicateFunctionDeclaration;
import org.example.parser.error.ExpectedBlockException;
import org.example.parser.error.ExpectedTypeDeclarationException;
import org.example.parser.error.MissingArgumentException;
import org.example.parser.error.MissingArithmeticExpression;
import org.example.parser.error.MissingExpressionException;
import org.example.parser.error.MissingIdentifierException;
import org.example.parser.error.MissingLogicalExpressionException;
import org.example.parser.error.MissingStatementException;
import org.example.parser.error.MissingTokenException;
import org.example.parser.error.MissingTupleElementException;
import org.example.parser.error.MissingTupleExpressionException;
import org.example.parser.error.MissingTypeDeclaration;
import org.example.parser.error.ParserException;
import org.example.parser.error.UnexpectedTokenException;
import org.example.token.Position;
import org.example.token.Token;
import org.example.token.TokenType;

@Slf4j
@RequiredArgsConstructor
public class ParserImpl implements Parser {

	private final Lexer lexer;
	private final ErrorHandler errorHandler;

	private Token currentToken;

	private void nextToken() {
		currentToken = lexer.nextToken();
	}

	/**
	 * PROGRAM = {COMMENT | FUNCTION_DEFINITION | DECLARATION | ";"};
	 */
	@Override
	public Program parseProgram() {
		nextToken();
		var functionDefinitions = new HashMap<String, FunctionDefinitionStatement>();
		var declarations = new HashMap<String, DeclarationStatement>();

		boolean parse;
		do {
			parse = fillIn(this::parseFunctionDefinition, it -> storeFunction(it, functionDefinitions))
					.or(() -> fillIn(this::parseDeclarationStatement, it -> storeDeclaration(it, declarations)))
					.orElseGet(() -> skipIf(TokenType.SEMICOLON));
		} while (parse);

		if (currentToken.getType() != TokenType.END_OF_FILE) {
			handleNonCriticalException(TokenType.END_OF_FILE);
		}

		return new Program(functionDefinitions, declarations);
	}

	private void storeFunction(FunctionDefinitionStatement statement, Map<String, FunctionDefinitionStatement> functionDefinitions) {
		if (functionDefinitions.containsKey(statement.getName())) {
			throw handleCriticalException(token -> new DuplicateFunctionDeclaration(token, statement.getName()));
		}
		functionDefinitions.put(statement.getName(), statement);
	}

	private void storeDeclaration(DeclarationStatement statement, Map<String, DeclarationStatement> declarations) {
		var identifier = statement.getArgument().getName();
		if (declarations.containsKey(identifier)) {
			throw handleCriticalException(token -> new DuplicateDeclaration(token, identifier));
		}
		declarations.put(identifier, statement);
	}

	/**
	 * FUNCTION_DEFINITION = "fun", IDENTIFIER, "(", [ARGUMENT_LIST], ")", [":", TYPE_DECLARATION], BLOCK;
	 */
	private Optional<FunctionDefinitionStatement> parseFunctionDefinition() {
		if (!skipIf(TokenType.FUNCTION_DEFINITION)) {
			return Optional.empty();
		}

		var position = currentToken.getPosition();
		var name = getIdentifier();

		handleSkip(TokenType.OPEN_ROUND_PARENTHESES);

		var arguments = parseArguments();

		handleSkip(TokenType.CLOSED_ROUND_PARENTHESES);

		var returnType = skipIf(TokenType.COLON)
				? parseTypeDeclaration()
				.orElseGet(() -> {
					handleNonCriticalException(ExpectedTypeDeclarationException::new);
					return new TypeDeclaration(ValueType.VOID);
				})
				: new TypeDeclaration(ValueType.VOID);

		var block = parseBlock()
				.orElseGet(() -> {
					handleNonCriticalException(ExpectedBlockException::new);
					return new BlockStatement(List.of(), currentToken.getPosition());
				});

		return Optional.of(new FunctionDefinitionStatement(name, arguments, returnType, block, position));
	}

	/**
	 * ARGUMENT_LIST = ARGUMENT_DECLARATION, {",", ARGUMENT_DECLARATION};
	 */
	private List<Argument> parseArguments() {
		var firstArgument = parseArgument();
		var arguments = new ArrayList<Argument>();
		if (firstArgument.isEmpty()) {
			return arguments;
		}
		arguments.add(firstArgument.get());

		while (skipIf(TokenType.COMMA)) {
			var argument = retrieveItem(this::parseArgument, MissingArgumentException::new);
			arguments.add(argument);
		}

		return arguments;
	}

	/**
	 * ARGUMENT_DECLARATION = IDENTIFIER, ":", TYPE_DECLARATION;
	 */
	private Optional<Argument> parseArgument() {
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			return Optional.empty();
		}

		var identifier = getIdentifier();

		handleSkip(TokenType.COLON);

		var type = parseTypeDeclaration().orElseThrow(() -> handleCriticalException(MissingTypeDeclaration::new));

		return Optional.of(new Argument(identifier, type));
	}

	/**
	 * TYPE_DECLARATION =
	 * <p>&emsp;&emsp;&emsp;SIMPLE_TYPE
	 * <p>&emsp;&emsp;&emsp;| COMPLEX_TYPE, "<", TYPE_DECLARATION, {",", TYPE_DECLARATION} ,">";
	 */
	private Optional<TypeDeclaration> parseTypeDeclaration() {
		var valueType = ValueType.of(currentToken.getType());
		if (valueType.isEmpty()) {
			return Optional.empty();
		}
		nextToken();
		var type = valueType.get();

		var types = new ArrayList<TypeDeclaration>();

		if (!type.isComplex()) {
			return Optional.of(new TypeDeclaration(type, types));
		}

		handleSkip(TokenType.LESS);

		do {
			var declaration = retrieveItem(this::parseTypeDeclaration, MissingTypeDeclaration::new);
			types.add(declaration);
		} while (skipIf(TokenType.COMMA));

		handleSkip(TokenType.GREATER);
		return Optional.of(new TypeDeclaration(type, types));
	}

	/**
	 * DECLARATION = TYPE_DECLARATION, IDENTIFIER, "=", EXPRESSION, ";";
	 */
	private Optional<DeclarationStatement> parseDeclarationStatement() {
		var typeDeclaration = parseTypeDeclaration();
		if (typeDeclaration.isEmpty()) {
			return Optional.empty();
		}

		var position = currentToken.getPosition();
		var identifier = getIdentifier();

		handleSkip(TokenType.EQUALS);

		var expression = retrieveItem(this::parseExpression, MissingExpressionException::new);

		handleSkip(TokenType.SEMICOLON);

		return Optional.of(new DeclarationStatement(new Argument(identifier, typeDeclaration.get()), expression, position));
	}

	/**
	 * BLOCK = "{", {STATEMENT} "}";
	 */
	private Optional<BlockStatement> parseBlock() {
		var position = currentToken.getPosition();
		if (!skipIf(TokenType.OPEN_CURLY_PARENTHESES)) {
			return Optional.empty();
		}

		var statements = new ArrayList<Statement>();
		var statement = parseStatement();
		while (statement.isPresent()) {
			statements.add(statement.get());
			statement = parseStatement();
		}

		log.info("{}", currentToken);
		handleSkip(TokenType.CLOSED_CURLY_PARENTHESES);

		return Optional.of(new BlockStatement(statements, position));
	}

	private final List<Supplier<Optional<? extends Statement>>> statementSuppliers = List.of(
			this::parseIfStatement,
			this::parseWhileStatement,
			this::parseForStatement,
			this::parseDeclarationStatement,
			this::parseAssignmentStatementOrSingleExpression,
			this::parseReturnStatement,
			this::parseBlock
	);

	/**
	 * STATEMENT =
	 * <p>&emsp;&emsp;&emsp;IF_STATEMENT
	 * <p>&emsp;&emsp;&emsp;| WHILE_STATEMENT
	 * <p>&emsp;&emsp;&emsp;| FOR_STATEMENT
	 * <p>&emsp;&emsp;&emsp;| DECLARATION
	 * <p>&emsp;&emsp;&emsp;| ASSIGNMENT_OR_SINGLE_EXPRESSION
	 * <p>&emsp;&emsp;&emsp;| BLOCK
	 * <p>&emsp;&emsp;&emsp;| ";";
	 */
	@SuppressWarnings({"unchecked", "StatementWithEmptyBody"})
	private Optional<Statement> parseStatement() {
		for (var supplier : statementSuppliers) {
			var statement = supplier.get();
			if (statement.isPresent()) {
				return (Optional<Statement>) statement;
			}
		}
		while (skipIf(TokenType.SEMICOLON)) ;
		return Optional.empty();
	}

	/**
	 * IF_STATEMENT = "if", LOGICAL_EXPRESSION, STATEMENT, ["else", STATEMENT];
	 */
	private Optional<IfStatement> parseIfStatement() {
		var position = currentToken.getPosition();
		if (!skipIf(TokenType.IF)) {
			return Optional.empty();
		}

		var condition = retrieveItem(this::parseLogicalExpression, MissingExpressionException::new);

		var ifTrue = retrieveItem(this::parseStatement, MissingStatementException::new);

		Optional<Statement> ifFalse = skipIf(TokenType.ELSE)
				? parseStatement()
				: Optional.empty();

		return Optional.of(
				new IfStatement(condition, ifTrue, ifFalse.orElseGet(() -> new BlockStatement(List.of(), currentToken.getPosition())), position)
		);
	}

	/**
	 * WHILE_STATEMENT = "while", LOGICAL_EXPRESSION, STATEMENT;
	 */
	private Optional<WhileStatement> parseWhileStatement() {
		var position = currentToken.getPosition();
		if (!skipIf(TokenType.WHILE)) {
			return Optional.empty();
		}
		var condition = retrieveItem(this::parseLogicalExpression, MissingExpressionException::new);

		var statement = parseStatement()
				.orElseGet(() -> {
					handleNonCriticalException(MissingStatementException::new);
					return new BlockStatement(List.of(), currentToken.getPosition());
				});

		return Optional.of(
				new WhileStatement(condition, statement, position)
		);
	}

	/**
	 * FOR_STATEMENT = "for", "(", TYPE_DECLARATION, IDENTIFIER, ":", EXPRESSION, ")", STATEMENT;
	 */
	private Optional<ForStatement> parseForStatement() {
		var position = currentToken.getPosition();
		if (!skipIf(TokenType.FOR)) {
			return Optional.empty();
		}

		handleSkip(TokenType.OPEN_ROUND_PARENTHESES);

		var type = retrieveItem(this::parseTypeDeclaration, MissingTypeDeclaration::new);

		var identifier = getIdentifier();

		handleSkip(TokenType.COLON);

		var iterable = retrieveItem(this::parseExpression, MissingExpressionException::new);

		handleSkip(TokenType.CLOSED_ROUND_PARENTHESES);

		var body = parseStatement()
				.orElseGet(() -> {
					handleNonCriticalException(MissingStatementException::new);
					return new BlockStatement(List.of(), currentToken.getPosition());
				});

		return Optional.of(new ForStatement(new Argument(identifier, type), iterable, body, position));
	}

	/**
	 * ASSIGNMENT_OR_IDENTIFIER_EXPRESSION = [IDENTIFIER, "="], EXPRESSION, ";";
	 */
	private Optional<Statement> parseAssignmentStatementOrSingleExpression() {
		var optionalExpression = parseExpression();
		if (optionalExpression.isEmpty()) {
			return Optional.empty();
		}
		var expression = optionalExpression.get();

		if (!skipIf(TokenType.EQUALS)) {
			handleSkip(TokenType.SEMICOLON);
			return optionalExpression.map(Statement.class::cast);
		}

		if (expression instanceof IdentifierExpression identifier) {
			var value = retrieveItem(this::parseExpression, MissingExpressionException::new);
			handleSkip(TokenType.SEMICOLON);
			return Optional.of(
					new AssignmentStatement(
							identifier.getName(),
							value,
							expression.getPosition()
					)
			);
		}
		throw handleCriticalException(CannotAssignValueToExpressionException::new);
	}

	/**
	 * RETURN_STATEMENT = "return", EXPRESSION, ";";
	 */
	private Optional<ReturnStatement> parseReturnStatement() {
		var position = currentToken.getPosition();
		if (!skipIf(TokenType.RETURN)) {
			return Optional.empty();
		}

		var expression = retrieveItem(this::parseExpression, MissingExpressionException::new);

		handleSkip(TokenType.SEMICOLON);

		return Optional.of(new ReturnStatement(expression, position));
	}

	/**
	 * EXPRESSION = LOGICAL_EXPRESSION;
	 */
	private Optional<Expression> parseExpression() {
		return parseLogicalExpression();
	}

	/**
	 * LOGICAL_EXPRESSION = LOGICAL_OR_EXPRESSION;
	 */
	private Optional<Expression> parseLogicalExpression() {
		return parseLogicalOrExpression();
	}

	private Optional<Expression> parseGenericLogicalExpression(
			TokenType type,
			Supplier<Optional<Expression>> supplier,
			Function3<Expression, Expression, Position, Expression> wrapper
	) {
		var leftOptional = supplier.get();
		if (leftOptional.isEmpty()) {
			return Optional.empty();
		}

		var left = leftOptional.get();
		var position = currentToken.getPosition();
		while (skipIf(type)) {
			var right = retrieveItem(supplier, MissingLogicalExpressionException::new);
			left = wrapper.apply(left, right, position);
			position = currentToken.getPosition();
		}

		return Optional.of(left);
	}

	/**
	 * LOGICAL_OR_EXPRESSION   = LOGICAL_AND_EXPRESSION, {"or", LOGICAL_AND_EXPRESSION};
	 */
	private Optional<Expression> parseLogicalOrExpression() {
		return parseGenericLogicalExpression(TokenType.OR, this::parseLogicalAndExpression, OrLogicalExpression::new);
	}

	/**
	 * LOGICAL_AND_EXPRESSION  = RELATION, {"and", RELATION};
	 */
	private Optional<Expression> parseLogicalAndExpression() {
		return parseGenericLogicalExpression(TokenType.AND, this::parseRelation, AndLogicalExpression::new);
	}

	private final Map<TokenType, Function3<Expression, Expression, Position, Expression>> relationExpressions = Map.of(
			TokenType.LESS, Function3.of(LessLogicalExpression::new),
			TokenType.LESS_EQUAL, Function3.of(LessEqualLogicalExpression::new),
			TokenType.EQUALITY, Function3.of(EqualityLogicalExpression::new),
			TokenType.GREATER_EQUAL, Function3.of(GreaterEqualLogicalExpression::new),
			TokenType.GREATER, Function3.of(GreaterLogicalExpression::new),
			TokenType.INEQUALITY, Function3.of(InequalityLogicalExpression::new)
	);

	/**
	 * RELATION = ["not"], (BOOLEAN | ARITHMETIC_EXPRESSION, [relation_operator, ARITHMETIC_EXPRESSION]);
	 */
	private Optional<Expression> parseRelation() {
		var position = currentToken.getPosition();
		var negate = skipIf(TokenType.NOT);

		if (skipIf(TokenType.BOOLEAN_TRUE)) {
			return Optional.of(new BooleanValue(!negate, currentToken.getPosition()));
		}
		if (skipIf(TokenType.BOOLEAN_FALSE)) {
			return Optional.of(new BooleanValue(negate, currentToken.getPosition()));
		}

		var expression = parseArithmeticExpression();
		if (expression.isEmpty()) {
			return Optional.empty();
		}

		var type = currentToken.getType();
		if (!relationExpressions.containsKey(type)) {
			return expression;
		}
		var constructor = relationExpressions.get(type);
		var relationPosition = currentToken.getPosition();
		nextToken();

		var right = retrieveItem(this::parseArithmeticExpression, MissingArithmeticExpression::new);

		var logicalExpression = constructor.apply(expression.get(), right, relationPosition);

		return Optional.of(negate ? new NegateLogicalExpression(logicalExpression, position) : logicalExpression);
	}

	/**
	 * ARITHMETIC_EXPRESSION   = FACTOR, {addition_operator, FACTOR};
	 */
	private Optional<Expression> parseArithmeticExpression() {
		var leftOptional = parseFactor();
		if (leftOptional.isEmpty()) {
			return Optional.empty();
		}
		var left = leftOptional.get();

		while (currentToken.getType() != TokenType.END_OF_FILE) {
			var position = currentToken.getPosition();
			if (skipIf(TokenType.PLUS)) {
				var right = retrieveItem(this::parseFactor, MissingArithmeticExpression::new);
				left = new AddArithmeticExpression(left, right, position);
			} else if (skipIf(TokenType.MINUS)) {
				var right = retrieveItem(this::parseFactor, MissingArithmeticExpression::new);
				left = new SubtractArithmeticExpression(left, right, position);
			} else {
				break;
			}
		}

		return Optional.of(left);
	}

	/**
	 * FACTOR = TERM, {multiplication_operator, TERM};
	 */
	private Optional<Expression> parseFactor() {
		var leftOptional = parseTerm();
		if (leftOptional.isEmpty()) {
			return Optional.empty();
		}
		var left = leftOptional.get();

		while (currentToken.getType() != TokenType.END_OF_FILE) {
			var position = currentToken.getPosition();
			if (skipIf(TokenType.TIMES)) {
				var right = retrieveItem(this::parseTerm, MissingArithmeticExpression::new);
				left = new MultiplyArithmeticExpression(left, right, position);
			} else if (skipIf(TokenType.DIVIDE)) {
				var right = retrieveItem(this::parseTerm, MissingArithmeticExpression::new);
				left = new DivideArithmeticExpression(left, right, position);
			} else {
				break;
			}
		}
		return Optional.of(left);
	}

	private final List<Supplier<Optional<Expression>>> termSuppliers = List.of(
			this::parseSimpleTypeExpression,
			this::parseTupleOrMethodOrMapCall
	);

	/**
	 * TERM = ["-"], (SIMPLE_TYPE | TUPLE_OR_METHOD_CALL);
	 */
	private Optional<Expression> parseTerm() {
		var position = currentToken.getPosition();
		var negate = skipIf(TokenType.MINUS);

		for (var function : termSuppliers) {
			var expression = function.get();
			if (expression.isPresent()) {
				return expression.map(it -> negate ? new NegationArithmeticExpression(it, position) : it);
			}
		}
		return Optional.empty();
	}

	private final List<Pair<Predicate<TokenType>, BiFunction<?, Position, Expression>>> simpleTypeParser = List.of(
			Pair.of(it -> it == TokenType.INTEGER_CONSTANT, (BiFunction<Integer, Position, Expression>) IntegerValue::new),
			Pair.of(it -> it == TokenType.FLOATING_POINT_CONSTANT, (BiFunction<Double, Position, Expression>) FloatingPointValue::new),
			Pair.of(LexerUtility.STRINGS::containsValue, (BiFunction<String, Position, Expression>) StringValue::new)
	);

	/**
	 * SIMPLE_TYPE = NUMBER | STRING;
	 */
	private Optional<Expression> parseSimpleTypeExpression() {
		var type = currentToken.getType();
		for (var pair : simpleTypeParser) {
			if (pair.getKey().test(type)) {
				var result = pair.getValue().apply(currentToken.getValue(), currentToken.getPosition());
				nextToken();
				return Optional.of(result);
			}
		}
		return Optional.empty();
	}

	/**
	 * TUPLE_METHOD_MAP_CALL = SIMPLE_EXPRESSION, { (".", IDENTIFIER, [FUNCTION_ARGUMENTS] | "[", EXPRESSION,"]" ) };
	 */
	private Optional<Expression> parseTupleOrMethodOrMapCall() {
		var optionalExpression = parseSimpleExpression();
		if (optionalExpression.isEmpty()) {
			return Optional.empty();
		}
		var expression = optionalExpression.get();
		var position = currentToken.getPosition();
		var parse = true;
		while (parse) {
			if (skipIf(TokenType.DOT)) {
				var identifier = getIdentifier();
				var parameters = parseFunctionArguments();
				expression = parameters.isPresent()
						? new MethodCallExpression(expression, new FunctionCallExpression(identifier, parameters.get(), position), position)
						: new TupleCallExpression(expression, identifier, position);
			} else if (skipIf(TokenType.OPEN_SQUARE_PARENTHESES)) {
				var argument = retrieveItem(this::parseExpression, MissingExpressionException::new);
				handleSkip(TokenType.CLOSED_SQUARE_PARENTHESES);
				var functionCall = new FunctionCallExpression("operator[]", List.of(argument), position);
				expression = new MethodCallExpression(expression, functionCall, position);
			} else {
				parse = false;
			}
			position = currentToken.getPosition();
		}
		return Optional.of(expression);
	}

	/**
	 * FUNCTION_ARGUMENTS = "(", [EXPRESSION, {",", EXPRESSION}], ")";
	 */
	private Optional<List<Expression>> parseFunctionArguments() {
		if (!skipIf(TokenType.OPEN_ROUND_PARENTHESES)) {
			return Optional.empty();
		}

		var arguments = new ArrayList<Expression>();
		if (currentToken.getType() != TokenType.CLOSED_ROUND_PARENTHESES) {
			do {
				var argument = retrieveItem(this::parseExpression, MissingExpressionException::new);
				arguments.add(argument);
			} while (skipIf(TokenType.COMMA));
		}

		handleSkip(TokenType.CLOSED_ROUND_PARENTHESES);

		return Optional.of(arguments);
	}

	private final List<Supplier<Optional<Expression>>> simpleExpressionSuppliers = List.of(
			this::parseIdentifierOrFunctionCall,
			this::parseSelectExpression,
			this::parseMapExpression,
			this::parseStandAloneTupleExpression,
			this::parseExplicitCast,
			this::parseParenthesesExpression
	);

	/**
	 * SIMPLE_EXPRESSION
	 * <p>&emsp;&emsp;&emsp;= IDENTIFIER_OR_FUNCTION_CALL
	 * <p>&emsp;&emsp;&emsp;| SELECT_EXPRESSION
	 * <p>&emsp;&emsp;&emsp;| STANDALONE_TUPLE_EXP
	 * <p>&emsp;&emsp;&emsp;| MAP_EXPRESSION
	 * <p>&emsp;&emsp;&emsp;| EXPLICIT_CAST
	 * <p>&emsp;&emsp;&emsp;| "(", EXPRESSION, ")";
	 */
	private Optional<Expression> parseSimpleExpression() {
		for (var supplier : simpleExpressionSuppliers) {
			var expression = supplier.get();
			if (expression.isPresent()) {
				return expression;
			}
		}
		return Optional.empty();
	}

	/**
	 * IDENTIFIER_OR_FUNCTION_CALL = IDENTIFIER, [FUNCTION_ARGUMENTS];
	 */
	private Optional<Expression> parseIdentifierOrFunctionCall() {
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			return Optional.empty();
		}
		var position = currentToken.getPosition();
		var name = getIdentifier();

		var arguments = parseFunctionArguments();

		var expression = arguments.isPresent()
				? new FunctionCallExpression(name, arguments.get(), position)
				: new IdentifierExpression(name, position);

		return Optional.of(expression);
	}

	/**
	 * SELECT_EXPRESSION
	 * <p>= "SELECT", TUPLE_EXPRESSION, "FROM", TUPLE_ELEMENT,
	 * <p>{"JOIN", TUPLE_ELEMENT, ["ON", EXPRESSION]},
	 * <p>["WHERE", EXPRESSION],
	 * <p>["GROUP", "BY", EXPRESSION, {",", EXPRESSION}, ["HAVING", EXPRESSION]],
	 * <p>["ORDER", "BY", EXPRESSION, ["ASC" | "DESC"], {"," ORDER_BY_EXPRESSION}];
	 */
	private Optional<Expression> parseSelectExpression() {
		var position = currentToken.getPosition();
		if (!skipIf(TokenType.SELECT)) {
			return Optional.empty();
		}
		var select = retrieveItem(this::parseTupleExpression, MissingTupleExpressionException::new);
		handleSkip(TokenType.FROM);
		var from = retrieveItem(this::parseTupleElement, MissingTupleElementException::new);
		var join = new ArrayList<Tuple3<String, Expression, Expression>>();

		while (skipIf(TokenType.JOIN)) {
			var joinTable = retrieveItem(this::parseTupleElement, MissingTupleElementException::new);

			var on = skipIf(TokenType.ON)
					? retrieveItem(this::parseLogicalExpression, MissingLogicalExpressionException::new)
					: new BooleanValue(true, currentToken.getPosition());

			join.add(Tuple.of(joinTable.getKey(), joinTable.getValue(), on));
		}

		var where = skipIf(TokenType.WHERE)
				? retrieveItem(this::parseLogicalExpression, MissingLogicalExpressionException::new)
				: new BooleanValue(true, currentToken.getPosition());

		var groupByResult = parseGroupBy();
		var groupBy = groupByResult.getKey();
		var having = groupByResult.getValue();

		var orderBy = parseOrderBy();

		return Optional.of(
				new SelectExpression(select, from, join, where, groupBy, having, orderBy, position)
		);
	}

	private Pair<List<Expression>, Expression> parseGroupBy() {
		var groupBy = new ArrayList<Expression>();
		var having = (Expression) new BooleanValue(true, currentToken.getPosition());
		if (skipIf(TokenType.GROUP) && skipIf(TokenType.BY)) {
			do {
				var expression = retrieveItem(this::parseExpression, MissingExpressionException::new);
				groupBy.add(expression);
			} while (skipIf(TokenType.COMMA));

			having = skipIf(TokenType.HAVING)
					? retrieveItem(this::parseLogicalExpression, MissingLogicalExpressionException::new)
					: having;
		}
		return Pair.of(groupBy, having);
	}

	private List<Pair<Expression, Boolean>> parseOrderBy() {
		var orderBy = new ArrayList<Pair<Expression, Boolean>>();
		if (skipIf(TokenType.ORDER) && skipIf(TokenType.BY)) {
			do {
				var expression = retrieveItem(this::parseExpression, MissingExpressionException::new);
				var ascending = true;
				if (skipIf(TokenType.DESCENDING)) {
					ascending = false;
				} else {
					skipIf(TokenType.ASCENDING);
				}
				orderBy.add(Pair.of(expression, ascending));
			} while (skipIf(TokenType.COMMA));
		}
		return orderBy;
	}

	/**
	 * MAP_EXPRESSION = "[", [EXPRESSION, ":", EXPRESSION, {",", EXPRESSION, ":", EXPRESSION}], "]";
	 */
	private Optional<Expression> parseMapExpression() {
		var position = currentToken.getPosition();
		if (!skipIf(TokenType.OPEN_SQUARE_PARENTHESES)) {
			return Optional.empty();
		}

		var map = new HashMap<Expression, Expression>();
		var firstKey = parseExpression();
		if (firstKey.isPresent()) {
			handleSkip(TokenType.COLON);
			var firstValue = retrieveItem(this::parseExpression, MissingExpressionException::new);
			map.put(firstKey.get(), firstValue);

			while (skipIf(TokenType.COMMA)) {
				var key = retrieveItem(this::parseExpression, MissingExpressionException::new);
				handleSkip(TokenType.COLON);
				var value = retrieveItem(this::parseExpression, MissingExpressionException::new);
				map.put(key, value);
			}
		}

		handleSkip(TokenType.CLOSED_SQUARE_PARENTHESES);
		return Optional.of(
				new MapExpression(map, position)
		);
	}

	/**
	 * STANDALONE_TUPLE_EXP = "|", TUPLE_EXPRESSION, "|";
	 */
	private Optional<Expression> parseStandAloneTupleExpression() {
		if (!skipIf(TokenType.VERTICAL_BAR_PARENTHESES)) {
			return Optional.empty();
		}
		var expression = parseTupleExpression();
		handleSkip(TokenType.VERTICAL_BAR_PARENTHESES);
		return expression.map(Expression.class::cast);
	}

	/**
	 * TUPLE_EXPRESSION = TUPLE_ELEMENT, {",", TUPLE_ELEMENT};
	 */
	private Optional<TupleExpression> parseTupleExpression() {
		var position = currentToken.getPosition();
		var firstElement = parseTupleElement();
		if (firstElement.isEmpty()) {
			return Optional.empty();
		}

		var elements = new HashMap<String, Expression>();
		var element = firstElement.get();
		elements.put(element.getKey(), element.getValue());

		while (skipIf(TokenType.COMMA)) {
			element = retrieveItem(this::parseTupleElement, MissingTupleElementException::new);
			elements.put(element.getKey(), element.getValue());
		}

		return Optional.of(
				new TupleExpression(elements, position)
		);
	}

	/**
	 * TUPLE_ELEMENT = EXPRESSION, "AS", IDENTIFIER;
	 */
	private Optional<Map.Entry<String, Expression>> parseTupleElement() {
		var expression = parseExpression();
		if (expression.isEmpty()) {
			return Optional.empty();
		}
		handleSkip(TokenType.AS);
		var identifier = getIdentifier();
		return Optional.of(Map.entry(identifier, expression.get()));
	}

	/**
	 * EXPLICIT_CAST = "@", TYPE_DECLARATION, EXPRESSION;
	 */
	private Optional<Expression> parseExplicitCast() {
		var position = currentToken.getPosition();
		if (!skipIf(TokenType.EXPLICIT_CAST)) {
			return Optional.empty();
		}
		var type = retrieveItem(this::parseTypeDeclaration, MissingTypeDeclaration::new);
		var expression = retrieveItem(this::parseExpression, MissingExpressionException::new);
		return Optional.of(new ExplicitCastExpression(type, expression, position));
	}

	/**
	 * PARENTHESES_EXPRESSION = "(", EXPRESSION, ")";
	 */
	private Optional<Expression> parseParenthesesExpression() {
		if (!skipIf(TokenType.OPEN_ROUND_PARENTHESES)) {
			return Optional.empty();
		}
		var expression = parseExpression();
		handleSkip(TokenType.CLOSED_ROUND_PARENTHESES);
		return expression;
	}


	private String getIdentifier() {
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			throw handleCriticalException(MissingIdentifierException::new);
		}
		var identifier = currentToken.<String>getValue();
		nextToken();
		return identifier;
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

	private void handleSkip(TokenType expected) {
		if (!skipIf(expected)) {
			if (expected == TokenType.SEMICOLON) {
				handleNonCriticalException(TokenType.SEMICOLON);
			} else {
				throw handleCriticalException(token -> new MissingTokenException(token, expected));
			}
		}
	}

	@NonNull
	private <T> T retrieveItem(@NonNull Supplier<Optional<T>> supplier, Function<Token, ? extends CriticalParserException> exceptionProvider) {
		var optional = supplier.get();
		if (optional.isEmpty()) {
			throw handleCriticalException(exceptionProvider);
		}
		return optional.get();
	}

	private CriticalParserException handleCriticalException(Function<Token, ? extends CriticalParserException> exceptionProvider) {
		var exception = exceptionProvider.apply(currentToken);
		errorHandler.handleParserException(exception);
		throw exception;
	}

	private void handleNonCriticalException(TokenType expected) {
		var exception = new UnexpectedTokenException(expected, currentToken);
		errorHandler.handleParserException(exception);
	}

	private void handleNonCriticalException(Function<Token, ? extends ParserException> exceptionProvider) {
		var exception = exceptionProvider.apply(currentToken);
		errorHandler.handleParserException(exception);
	}
}
