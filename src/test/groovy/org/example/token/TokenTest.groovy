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
		token                                                      || value
		new StringToken(TokenType.IDENTIFIER, position, "value")   || "value"
		new IntegerToken(position, 1)                              || 1
		new FloatingPointToken(position, 1.25)                     || 1.25
		new KeywordToken(TokenType.FUNCTION_DEFINITION, position)  || null
		new BooleanToken(TokenType.BOOLEAN_TRUE, position, true)   || true
		new BooleanToken(TokenType.BOOLEAN_FALSE, position, false) || false
	}
}
