package org.example.error


import org.example.lexer.error.InvalidTokenException
import org.example.parser.error.UnknownTokenException
import org.example.token.Position
import org.example.token.type.BooleanToken
import spock.lang.Specification

class ErrorHandlerTest extends Specification {

	var errorHandler = new ErrorHandlerImpl()

	def 'Should handle lexer errors correctly'() {
		when:
		errorHandler.handleLexerException(new InvalidTokenException("Test", new Position()))

		then:
		noExceptionThrown()
	}

	def 'Should handle parser errors correctly'() {
		when:
		errorHandler.handleParserException(new UnknownTokenException(new BooleanToken(new Position(), true)))

		then:
		noExceptionThrown()
	}

	def 'Should throw an error when too many exceptions occur'() {
		when:
		for (i in 0..ErrorHandlerConfiguration.MAX_ERRORS) {
			errorHandler.handleLexerException(new InvalidTokenException("Test", new Position(0, i)))
		}
		then:
		thrown TooManyExceptionsException
	}
}
