package org.example.token

import org.example.token.type.*
import spock.lang.Specification

class TokenTest extends Specification {

	var static position = Position.builder()
			.line(0)
			.characterNumber(0)
			.build()

	def 'Should return correct value'() {
		expect:
		token.getValue() == value

		where:
		token                                                     || value
		new StringToken(TokenType.IDENTIFIER, position, "value")  || "value"
		new IntegerToken(position, 1)                             || 1
		new FloatingPointToken(position, 1.25)                    || 1.25
		new KeywordToken(TokenType.FUNCTION_DEFINITION, position) || null
		new BooleanToken(position, true)                          || true
	}

	def 'Should return correct precedence'() {
		expect:
		token.getPrecedence() == value

		where:
		token                                                     || value
		new StringToken(TokenType.IDENTIFIER, position, "value")  || -1
		new IntegerToken(position, 1)                             || -1
		new FloatingPointToken(position, 1.25)                    || -1
		new KeywordToken(TokenType.FUNCTION_DEFINITION, position) || -1
		new KeywordToken(TokenType.PLUS, position)                || TokenType.PLUS.getPrecedence()
		new KeywordToken(TokenType.TIMES, position)               || TokenType.TIMES.getPrecedence()
		new BooleanToken(position, true)                          || -1
	}
}
