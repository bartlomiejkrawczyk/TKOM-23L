package org.example.error

import org.example.lexer.LexerImpl
import org.example.token.TokenType
import spock.lang.Specification


class ErrorHandlerIntegrationTest extends Specification {

	var errorHandler = new ErrorHandlerImpl()

	def 'Should log errors correctly'() {
		given:
		var content = "String 🙁 = \"Ala ma kota\";\nString 🙁 = \"Ala ma kota\";"
		var reader = new StringReader(content)
		var lexer = new LexerImpl(reader, errorHandler)
		while (lexer.nextToken().getType() != TokenType.END_OF_FILE) {
			// do nothing
		}
		reader.reset()
		when:
		errorHandler.showExceptions(reader)
		then:
		noExceptionThrown()
	}
}
