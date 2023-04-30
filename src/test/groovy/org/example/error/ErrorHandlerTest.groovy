package org.example.error

import org.example.lexer.error.InvalidTokenException
import org.example.parser.error.UnknownTokenException
import org.example.token.Position
import org.example.token.type.IntegerToken
import spock.lang.Specification
import spock.lang.Subject

class ErrorHandlerTest extends Specification {

	@Subject
	var errorHandler = new ErrorHandlerImpl()

	def 'Should handle lexer errors correctly'() {
		when:
		errorHandler.handleLexerException(new InvalidTokenException("Test", new Position()))

		then:
		noExceptionThrown()
	}

	def 'Should handle parser errors correctly'() {
		when:
		errorHandler.handleParserException(new UnknownTokenException(new IntegerToken(new Position(), 1)))

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
