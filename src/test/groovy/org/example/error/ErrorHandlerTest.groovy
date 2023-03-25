package org.example.error

import org.example.lexer.error.InvalidTokenException
import org.example.parser.error.UnknownTokenException
import org.example.token.Position
import org.example.token.Token
import spock.lang.Specification


class ErrorHandlerTest extends Specification {

	var errorHandler = new ErrorHandlerImpl()

	def 'Should handle lexer errors correctly'() {
		when:
		errorHandler.handleLexerException(new InvalidTokenException("Test", Position.builder().build()))

		then:
		noExceptionThrown()
	}

	def 'Should handle parser errors correctly'() {
		when:
		errorHandler.handleParserException(new UnknownTokenException(Mock(Token.class)))

		then:
		noExceptionThrown()
	}

	// TODO: test for errors cap reached
	// TODO: test if errors are printed nicely

}
