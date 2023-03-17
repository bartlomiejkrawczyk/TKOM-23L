package org.example.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.example.ast.Expression;
import org.example.ast.ValueType;
import org.example.ast.expression.BinaryExpression;
import org.example.ast.expression.FloatingPointExpression;
import org.example.ast.expression.FunctionCallExpression;
import org.example.ast.expression.FunctionExpression;
import org.example.ast.expression.IntegerExpression;
import org.example.ast.expression.PrototypeExpression;
import org.example.ast.expression.VariableExpression;
import org.example.error.ErrorHandler;
import org.example.lexer.Lexer;
import org.example.parser.error.UnexpectedTokenException;
import org.example.parser.error.UnknownTokenException;
import org.example.token.Token;
import org.example.token.TokenType;

public class ParserImpl implements Parser {

	private final Lexer lexer;
	private final ErrorHandler errorHandler;

	private Token currentToken;

	public ParserImpl(Lexer lexer, ErrorHandler errorHandler) {
		this.lexer = lexer;
		this.errorHandler = errorHandler;
		this.currentToken = lexer.nextToken();
	}

	private Token nextToken() {
		currentToken = lexer.nextToken();
		return currentToken;
	}

	@Override
	public Expression nextExpression() {
		if (currentToken.getType() == TokenType.END_OF_FILE) {
			return null;
		} else if (currentToken.getType() == TokenType.SEMICOLON) {
			nextToken();
		} else if (currentToken.getType() == TokenType.FUNCTION_DEFINITION) {
			parseDefinition();
		}
		return parseTopLevelExpression();
	}

	private Expression parseTopLevelExpression() {
		var expression = parseExpression();
		var prototype = new PrototypeExpression(StringUtils.EMPTY, Map.of());
		return new FunctionExpression(prototype, expression);
	}

	private Expression parseExpression() {
		var left = parsePrimaryExpression();
		return parseBinaryExpressionRight(0, left);
	}

	private Expression parsePrimaryExpression() {
		if (currentToken.getType() == TokenType.IDENTIFIER) {
			return parseIdentifierExpression();
		} else if (currentToken.getType() == TokenType.INTEGER_CONSTANT) {
			return parseIntegerExpression();
		} else if (currentToken.getType() == TokenType.FLOATING_POINT_CONSTANT) {
			return parseFloatingPointExpression();
		} else if (currentToken.getType() == TokenType.OPEN_ROUND_PARENTHESES) {
			return parseParenthesesExpression();
		}
		throw new UnknownTokenException(currentToken);
	}

	private Expression parseBinaryExpressionRight(int expressionPrecedence, Expression left) {
		// TODO: Refactor :^)
		while (true) {
			int tokenPrecedence = currentToken.getPrecedence();

			if (tokenPrecedence < expressionPrecedence) {
				return left;
			}

			var operation = currentToken;
			nextToken();
			var right = parsePrimaryExpression();

			var nextPrecedence = -1; // TODO: implement me

			if (tokenPrecedence < nextPrecedence) {
				right = parseBinaryExpressionRight(tokenPrecedence + 1, right);
			}

			left = new BinaryExpression(operation.getType(), left, right);
		}
	}

	private Expression parseIntegerExpression() {
		Integer value = currentToken.getValue();
		nextToken();
		return new IntegerExpression(value);
	}

	private Expression parseFloatingPointExpression() {
		Double value = currentToken.getValue();
		nextToken();
		return new FloatingPointExpression(value);
	}

	private Expression parseParenthesesExpression() {
		nextToken();
		var expression = parseExpression();
		if (currentToken.getType() != TokenType.CLOSED_ROUND_PARENTHESES) {
			throw new UnexpectedTokenException(TokenType.CLOSED_ROUND_PARENTHESES, currentToken);
		}
		nextToken();
		return expression;
	}

	private Expression parseIdentifierExpression() {
		var identifier = currentToken;
		nextToken();
		if (currentToken.getType() != TokenType.OPEN_ROUND_PARENTHESES) {
			return new VariableExpression(identifier.getValue());
		}
		nextToken();
		var arguments = new ArrayList<Expression>();
		// TODO: Refactor this atrocity
		if (currentToken.getType() != TokenType.CLOSED_ROUND_PARENTHESES) {
			while (true) {
				arguments.add(parseExpression());

				if (currentToken.getType() != TokenType.CLOSED_ROUND_PARENTHESES) {
					break;
				}

				if (currentToken.getType() != TokenType.COMMA) {
					throw new UnexpectedTokenException(TokenType.COMMA, currentToken);
				}
				nextToken();
			}
		}

		nextToken();

		return new FunctionCallExpression(identifier.getValue(), arguments);
	}

	private Expression parseDefinition() {
		nextToken();
		var prototype = parsePrototypeExpression();
		var body = parseExpression();
		return new FunctionExpression(prototype, body);
	}

	private PrototypeExpression parsePrototypeExpression() {
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			throw new UnexpectedTokenException(TokenType.IDENTIFIER, currentToken);
		}
		var functionName = (String) currentToken.getValue();
		nextToken();

		if (currentToken.getType() != TokenType.OPEN_ROUND_PARENTHESES) {
			throw new UnexpectedTokenException(TokenType.OPEN_ROUND_PARENTHESES, currentToken);
		}

		var argumentNames = new HashMap<String, ValueType>();

		while (nextToken().getType() == TokenType.IDENTIFIER) {

			var name = (String) currentToken.getValue();

			if (nextToken().getType() != TokenType.COLON) {
				throw new UnexpectedTokenException(TokenType.COLON, currentToken);
			}

			if (nextToken().getType() != TokenType.IDENTIFIER) {
				throw new UnexpectedTokenException(TokenType.IDENTIFIER, currentToken);
			}
			var type = ValueType.of(currentToken.getValue()); // TODO handle error

			argumentNames.put(name, type);

			if (nextToken().getType() != TokenType.COMMA) {
				break;
			}
		}

		if (currentToken.getType() != TokenType.CLOSED_ROUND_PARENTHESES) {
			throw new UnexpectedTokenException(TokenType.CLOSED_ROUND_PARENTHESES, currentToken);
		}

		nextToken();

		return new PrototypeExpression(functionName, argumentNames);
	}
}
