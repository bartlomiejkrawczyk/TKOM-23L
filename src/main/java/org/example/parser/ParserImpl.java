package org.example.parser;

import io.vavr.Function2;
import io.vavr.Tuple3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
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
import org.example.ast.expression.arithmetic.AddArithmeticExpression;
import org.example.ast.expression.arithmetic.DivideArithmeticExpression;
import org.example.ast.expression.arithmetic.MultiplyArithmeticExpression;
import org.example.ast.expression.arithmetic.NegationArithmeticExpression;
import org.example.ast.expression.arithmetic.SubtractArithmeticExpression;
import org.example.ast.expression.logical.AndLogicalExpression;
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
import org.example.ast.statement.WhileStatement;
import org.example.ast.type.BooleanValue;
import org.example.ast.type.FloatingPointValue;
import org.example.ast.type.IntegerValue;
import org.example.ast.type.StringValue;
import org.example.ast.type.TypeDeclaration;
import org.example.error.ErrorHandler;
import org.example.lexer.Lexer;
import org.example.lexer.LexerUtility;
import org.example.parser.error.CriticalParserException;
import org.example.parser.error.ExpectedTypeDeclarationException;
import org.example.parser.error.UnexpectedTokenException;
import org.example.token.Token;
import org.example.token.TokenType;

@Slf4j
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
		nextToken();
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
		handleSkip(TokenType.OPEN_ROUND_PARENTHESES);

		var arguments = new ArrayList<Argument>();
		if (currentToken.getType() != TokenType.CLOSED_ROUND_PARENTHESES) {
			do {
				var argument = retrieveItem(parseArgument(), "Missing argument");
				arguments.add(argument);
			} while (skipIf(TokenType.COMMA));
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
		}
		nextToken();

		var type = valueType.get();
		var types = new ArrayList<TypeDeclaration>();
		if (type.isComplex()) {
			handleSkip(TokenType.LESS);
			do {
				var declaration = retrieveItem(parseTypeDeclaration(), "Missing type declaration");
				types.add(declaration);
			} while (skipIf(TokenType.COMMA));
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
		if (!skipIf(TokenType.EQUALS)) {
			// TODO: parse single expression starting with provided identifier
		}
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

	private final List<Supplier<Optional<? extends Expression>>> expressionSuppliers = List.of(
			this::parseExpressionStartingWithParentheses,
			this::parseExpressionStartingWithIdentifier,
			this::parseExpressionStartingWithInteger,
			this::parseExpressionStartingWithFloatingPoint,
			this::parseExpressionStartingWithString,
			this::parseExpressionStartingWithBoolean,
			this::parseSelectExpression,
			this::parseMapExpression
	);

	private Optional<Expression> parseExpression() {
		for (var supplier : expressionSuppliers) {
			var expression = supplier.get();
			if (expression.isPresent()) {
				var result = parseExpressionStartingWithExpression(expression.get());
				return Optional.of(result);
			}
		}
		return Optional.empty();
	}

	private final List<UnaryOperator<Expression>> startingWithExpression = List.of(
			// TODO: (tuple call / method call) / arithmetic / logical
			this::parseTupleExpression,
			this::parseTupleOrMethodCall,
			this::parseMapCall
	);

	private Expression parseExpressionStartingWithExpression(Expression expression) {
		for (var function : startingWithExpression) {
			var result = function.apply(expression);
			if (result != expression) {
				return result;
			}
		}
		return expression;
	}

	private Optional<Expression> parseExpressionStartingWithParentheses() {
		if (!skipIf(TokenType.OPEN_ROUND_PARENTHESES)) {
			return Optional.empty();
		}
		var expression = retrieveItem(parseExpression(), "Missing expression");
		handleSkip(TokenType.CLOSED_ROUND_PARENTHESES);
		return Optional.of(expression);
	}

	private final List<Function<String, Optional<? extends Expression>>> startingWithIdentifier = List.of(
			this::parseFunctionCall
	);

	@SuppressWarnings("unchecked")
	private Optional<Expression> parseExpressionStartingWithIdentifier() {
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			return Optional.empty();
		}

		var identifier = currentToken.<String>getValue();
		nextToken();

		for (var function : startingWithIdentifier) {
			var result = (Optional<Expression>) function.apply(identifier);
			if (result.isPresent()) {
				return result;
			}
		}

		return Optional.of(new IdentifierExpression(identifier));
	}

	// TODO: simplify those \/ \/ \/ to a parametrized function
	private Optional<Expression> parseExpressionStartingWithInteger() {
		if (currentToken.getType() != TokenType.INTEGER_CONSTANT) {
			return Optional.empty();
		}

		var value = currentToken.<Integer>getValue();
		nextToken();

		return Optional.of(new IntegerValue(value));
	}

	private Optional<Expression> parseExpressionStartingWithFloatingPoint() {
		if (currentToken.getType() != TokenType.FLOATING_POINT_CONSTANT) {
			return Optional.empty();
		}

		var value = currentToken.<Double>getValue();
		nextToken();

		return Optional.of(new FloatingPointValue(value));
	}

	private Optional<Expression> parseExpressionStartingWithString() {
		if (!LexerUtility.STRINGS.containsValue(currentToken.getType())) {
			return Optional.empty();
		}

		var value = currentToken.<String>getValue();
		nextToken();

		return Optional.of(new StringValue(value));
	}

	private Optional<Expression> parseExpressionStartingWithBoolean() {
		if (!LexerUtility.BOOLEANS.contains(currentToken.getType())) {
			return Optional.empty();
		}

		var value = currentToken.<Boolean>getValue();
		nextToken();

		return Optional.of(new BooleanValue(value));
	}

	private Optional<IdentifierExpression> parseIdentifier() {
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			return Optional.empty();
		}
		var name = currentToken.<String>getValue();
		nextToken();
		return Optional.of(new IdentifierExpression(name));
	}

	private Optional<ArithmeticExpression> parseArithmeticExpression() {
		var leftOptional = parseFactor();
		if (leftOptional.isEmpty()) {
			return Optional.empty();
		}
		var left = leftOptional.get();

		while (currentToken.getType() != TokenType.END_OF_FILE) {
			if (skipIf(TokenType.PLUS)) {
				var right = retrieveItem(parseFactor(), "Missing factor");
				left = new AddArithmeticExpression(left, right);
			} else if (skipIf(TokenType.MINUS)) {
				var right = retrieveItem(parseFactor(), "Missing factor");
				left = new SubtractArithmeticExpression(left, right);
			} else {
				break;
			}
		}

		return Optional.of(left);
	}

	private Optional<ArithmeticExpression> parseFactor() {
		var leftOptional = parseTerm();
		if (leftOptional.isEmpty()) {
			return Optional.empty();
		}

		var left = leftOptional.get();

		while (currentToken.getType() != TokenType.END_OF_FILE) {
			if (skipIf(TokenType.TIMES)) {
				var right = retrieveItem(parseTerm(), "Missing term");
				left = new MultiplyArithmeticExpression(left, right);
			} else if (skipIf(TokenType.DIVIDE)) {
				var right = retrieveItem(parseTerm(), "Missing term");
				left = new DivideArithmeticExpression(left, right);
			} else {
				break;
			}
		}
		return Optional.of(left);
	}

	private Optional<ArithmeticExpression> parseTerm() {
		var negate = skipIf(TokenType.MINUS);

		Optional<ArithmeticExpression> expression = Optional.empty();
		// TODO: implement me!

		//				parseLiteral()
		//				.orElseGet(() -> parseIdentifier().get());


		return expression.map(it -> negate ? new NegationArithmeticExpression(it) : it);
	}

	private Optional<LogicalExpression> parseLogicalExpression() {
		var leftOptional = parseLogicFactor();
		if (leftOptional.isEmpty()) {
			return Optional.empty();
		}

		var left = leftOptional.get();

		while (currentToken.getType() != TokenType.END_OF_FILE) {
			if (skipIf(TokenType.OR)) {
				var right = retrieveItem(parseLogicFactor(), "Missing logic factor");
				left = new OrLogicalExpression(left, right);
			} else {
				break;
			}
		}

		return Optional.of(left);
	}

	private Optional<LogicalExpression> parseLogicFactor() {
		var leftOptional = parseRelation();
		if (leftOptional.isEmpty()) {
			return Optional.empty();
		}

		var left = leftOptional.get();

		while (currentToken.getType() != TokenType.END_OF_FILE) {
			if (skipIf(TokenType.AND)) {
				var right = retrieveItem(parseRelation(), "Missing relation");
				left = new AndLogicalExpression(left, right);
			} else {
				break;
			}
		}

		return Optional.of(left);
	}

	private final Map<TokenType, Function2<ArithmeticExpression, ArithmeticExpression, LogicalExpression>> relationExpressions = Map.of(
			TokenType.LESS, Function2.of(LessLogicalExpression::new),
			TokenType.LESS_EQUAL, Function2.of(LessEqualLogicalExpression::new),
			TokenType.EQUALITY, Function2.of(EqualityLogicalExpression::new),
			TokenType.GREATER_EQUAL, Function2.of(GreaterEqualLogicalExpression::new),
			TokenType.GREATER, Function2.of(GreaterLogicalExpression::new),
			TokenType.INEQUALITY, Function2.of(InequalityLogicalExpression::new)
	);

	private Optional<LogicalExpression> parseRelation() {
		var negate = skipIf(TokenType.NOT);

		if (skipIf(TokenType.BOOLEAN_TRUE)) {
			return Optional.of(new BooleanValue(!negate));
		}
		if (skipIf(TokenType.BOOLEAN_FALSE)) {
			return Optional.of(new BooleanValue(negate));
		}

		var leftOptionalExpression = parseArithmeticExpression();
		if (leftOptionalExpression.isEmpty()) {
			return Optional.empty();
		}
		var left = leftOptionalExpression.get();

		var type = currentToken.getType();
		if (!relationExpressions.containsKey(type)) {
			throw new CriticalParserException("Missing relation operator", currentToken);
		}
		var constructor = relationExpressions.get(type);
		var right = retrieveItem(parseArithmeticExpression(), "Missing arithmetic expression");

		return Optional.of(constructor.apply(left, right));
	}

	private Optional<FunctionCallExpression> parseFunctionCall(String name) {
		if (!skipIf(TokenType.OPEN_ROUND_PARENTHESES)) {
			return Optional.empty();
		}

		var arguments = new ArrayList<Expression>();
		if (currentToken.getType() != TokenType.CLOSED_ROUND_PARENTHESES) {
			do {
				var argument = retrieveItem(parseExpression(), "Missing argument expression");
				arguments.add(argument);
			} while (skipIf(TokenType.COMMA));
		}

		handleSkip(TokenType.CLOSED_ROUND_PARENTHESES);
		return Optional.of(
				new FunctionCallExpression(name, arguments)
		);
	}

	private Optional<FunctionCallExpression> parseFunctionCall() {
		var identifier = parseIdentifier();
		if (identifier.isEmpty()) {
			return Optional.empty();
		}
		var name = identifier.get();

		handleSkip(TokenType.OPEN_ROUND_PARENTHESES);

		var arguments = new ArrayList<Expression>();
		if (currentToken.getType() != TokenType.CLOSED_ROUND_PARENTHESES) {
			do {
				var argument = retrieveItem(parseExpression(), "Missing argument expression");
				arguments.add(argument);
			} while (skipIf(TokenType.COMMA));
		}

		handleSkip(TokenType.CLOSED_ROUND_PARENTHESES);
		return Optional.of(
				new FunctionCallExpression(name.getName(), arguments)
		);
	}

	private Expression parseTupleOrMethodCall(Expression expression) {
		if (!skipIf(TokenType.DOT)) {
			return expression;
		}
		var identifier = getIdentifierOrThrow();
		var function = parseFunctionCall(identifier);
		if (function.isPresent()) {
			return new MethodCallExpression(expression, function.get());
		} else {
			return new TupleCallExpression(expression, identifier);
		}
	}

	private Expression parseMapCall(Expression expression) {
		if (!skipIf(TokenType.OPEN_SQUARE_PARENTHESES)) {
			return expression;
		}
		var argument = retrieveItem(parseExpression(), "Missing expression in map []");
		handleSkip(TokenType.CLOSED_ROUND_PARENTHESES);
		var functionCall = new FunctionCallExpression("operator[]", List.of(argument));

		return new MethodCallExpression(expression, functionCall);
	}

	private Optional<SelectExpression> parseSelectExpression() {
		if (!skipIf(TokenType.SELECT)) {
			return Optional.empty();
		}
		var select = retrieveItem(parseTupleExpression(), "Missing tuple expression");
		handleSkip(TokenType.FROM);
		var from = retrieveItem(parseTupleElement(), "Missing tuple element");
		var join = new ArrayList<Tuple3<String, Expression, LogicalExpression>>();

		var where = skipIf(TokenType.WHERE)
				? retrieveItem(parseLogicalExpression(), "Missing 'where' logical expression")
				: new BooleanValue(true);

		var groupByResult = parseGroupBy();
		var groupBy = groupByResult.getKey();
		var having = groupByResult.getValue();

		var orderBy = parseOrderBy();

		return Optional.of(
				new SelectExpression(select, from, join, where, groupBy, having, orderBy)
		);
	}

	private Pair<List<Expression>, LogicalExpression> parseGroupBy() {
		var groupBy = new ArrayList<Expression>();
		var having = (LogicalExpression) new BooleanValue(true);
		if (skipIf(TokenType.GROUP) && skipIf(TokenType.BY)) {
			do {
				var expression = retrieveItem(parseExpression(), "Missing 'group by' expression");
				groupBy.add(expression);
			} while (skipIf(TokenType.COMMA));

			having = skipIf(TokenType.HAVING)
					? retrieveItem(parseLogicalExpression(), "Missing logical expression")
					: having;
		}
		return Pair.of(groupBy, having);
	}

	private List<Pair<Expression, Boolean>> parseOrderBy() {
		var orderBy = new ArrayList<Pair<Expression, Boolean>>();
		if (skipIf(TokenType.ORDER) && skipIf(TokenType.BY)) {
			do {
				var expression = retrieveItem(parseExpression(), "Missing 'group by' expression");
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

	private Expression parseTupleExpression(Expression expression) {
		if (!skipIf(TokenType.AS)) {
			return expression;
		}
		var identifier = getIdentifierOrThrow();
		var elements = new HashMap<String, Expression>();
		elements.put(identifier, expression);

		while (skipIf(TokenType.COMMA)) {
			var element = retrieveItem(parseTupleElement(), "Missing tuple element");
			elements.put(element.getKey(), element.getValue());
		}

		return new TupleExpression(elements);
	}

	private Optional<TupleExpression> parseTupleExpression() {
		var firstElement = parseTupleElement();
		if (firstElement.isEmpty()) {
			return Optional.empty();
		}

		var elements = new HashMap<String, Expression>();
		var element = firstElement.get();
		elements.put(element.getKey(), element.getValue());

		while (skipIf(TokenType.COMMA)) {
			element = retrieveItem(parseTupleElement(), "Missing tuple element");
			elements.put(element.getKey(), element.getValue());
		}

		return Optional.of(
				new TupleExpression(elements)
		);
	}

	private Optional<Map.Entry<String, Expression>> parseTupleElement() {
		var expression = parseExpressionDedicatedForTuple();
		if (expression.isEmpty()) {
			return Optional.empty();
		}
		handleSkip(TokenType.AS);
		var identifier = getIdentifierOrThrow();
		return Optional.of(Map.entry(identifier, expression.get()));
	}

	@SuppressWarnings("unchecked")
	private Optional<Expression> parseExpressionDedicatedForTuple() {
		for (var supplier : expressionSuppliers) {
			var expression = supplier.get();
			if (expression.isPresent()) {
				if (currentToken.getType() != TokenType.AS) {
					var result = parseExpressionStartingWithExpression(expression.get());
					return Optional.of(result);
				}
				return (Optional<Expression>) expression;
			}
		}
		return Optional.empty();
	}

	private Optional<MapExpression> parseMapExpression() {
		if (!skipIf(TokenType.OPEN_CURLY_PARENTHESES)) {
			return Optional.empty();
		}

		var map = new HashMap<Expression, Expression>();
		var firstKey = parseExpression();
		if (firstKey.isPresent()) {
			handleSkip(TokenType.COLON);
			var firstValue = retrieveItem(parseExpression(), "Missing value expression from map entry");
			map.put(firstKey.get(), firstValue);

			while (skipIf(TokenType.COMMA)) {
				var key = retrieveItem(parseExpression(), "Missing key expression from map entry");
				handleSkip(TokenType.COLON);
				var value = retrieveItem(parseExpression(), "Missing value expression from map entry");
				map.put(key, value);
			}
		}

		handleSkip(TokenType.CLOSED_CURLY_PARENTHESES);
		return Optional.of(
				new MapExpression(map)
		);
	}

	private String getIdentifierOrThrow() {
		var identifier = Optional.of(currentToken).filter(it -> it.getType() == TokenType.IDENTIFIER);
		identifier.ifPresent(it -> nextToken());
		return retrieveItem(identifier, "Missing identifier").getValue();
	}
}
