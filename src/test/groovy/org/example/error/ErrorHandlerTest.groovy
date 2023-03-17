package org.example.error

import spock.lang.Specification


class ErrorHandlerTest extends Specification {

	var errorHandler = new ErrorHandlerImpl()

	def 'Should log lexer errors correctly'() {
		when:
		errorHandler.handleLexerException(new RuntimeException("Test"))

		then:
		noExceptionThrown()
	}

}
