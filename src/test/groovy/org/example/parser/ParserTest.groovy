package org.example.parser

import org.example.ast.Program
import org.example.ast.ValueType
import org.example.ast.expression.BlockStatement
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.ast.type.TypeDeclaration
import org.example.error.ErrorHandler
import org.example.lexer.Lexer
import org.example.token.Position
import org.example.token.Token
import org.example.token.TokenType
import org.example.token.type.KeywordToken
import org.example.token.type.StringToken
import spock.lang.Specification
import spock.lang.Subject

class ParserTest extends Specification {

	var position = Position.builder().characterNumber(1).line(1).build()

	Parser toParser(List<Token> tokens) {
		var errorHandler = Mock(ErrorHandler)
		var lexer = Mock(Lexer) {
			for (def token : tokens) {
				1 * nextToken() >> token
			}
		}
		return new ParserImpl(lexer, errorHandler)
	}

	def 'Should be able to parse empty program'() {
		given:
		var program = [new KeywordToken(TokenType.END_OF_FILE, position)]

		@Subject
		var parser = toParser(program)

		expect:
		parser.parseProgram() == new Program(Map.of(), List.of())
	}

	def 'Should be able to parse basic function definition'() {
		given:
		var program = [
				new KeywordToken(TokenType.FUNCTION_DEFINITION, position),
				new StringToken(TokenType.IDENTIFIER, position, "main"),
				new KeywordToken(TokenType.OPEN_ROUND_PARENTHESES, position),
				new KeywordToken(TokenType.CLOSED_ROUND_PARENTHESES, position),
				new KeywordToken(TokenType.OPEN_CURLY_PARENTHESES, position),
				new KeywordToken(TokenType.CLOSED_CURLY_PARENTHESES, position),
				new KeywordToken(TokenType.END_OF_FILE, position)
		]

		@Subject
		var parser = toParser(program)

		expect:
		parser.parseProgram() == new Program(
				Map.of(
						"main",
						new FunctionDefinitionStatement(
								"main",
								List.of(),
								new TypeDeclaration(ValueType.VOID), new BlockStatement(List.of(), position),
								position
						)
				),
				List.of()
		)
	}
}
