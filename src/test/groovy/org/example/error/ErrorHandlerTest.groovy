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

	def 'Should log parser errors correctly'() {
		when:
		errorHandler.handleParserException(new RuntimeException("Test"))

		then:
		noExceptionThrown()
	}

	def 'Should log semantic analysis errors correctly'() {
		when:
		errorHandler.handleSemanticAnalysisException(new RuntimeException("Test"))

		then:
		noExceptionThrown()
	}

}
