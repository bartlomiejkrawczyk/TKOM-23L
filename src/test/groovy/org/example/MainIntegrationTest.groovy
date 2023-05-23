package org.example

import org.example.error.ErrorHandler
import org.example.lexer.CommentFilterLexer
import org.example.lexer.LexerImpl
import org.example.parser.ParserImpl
import spock.lang.Specification


class MainIntegrationTest extends Specification {


	def 'Should parse working programs without exceptions'() {
		when:
		Main.main("./src/main/resources/" + file)

		then:
		noExceptionThrown()

		where:
		file << ["query.txt", "test.txt", "math.txt"]
	}

	def 'Should be able to print program without exceptions'() {
		var errorHandler = Mock(ErrorHandler)
		var reader = new FileReader("./src/main/resources/$file")
		var lexer = new LexerImpl(reader, errorHandler)
		var filter = new CommentFilterLexer(lexer)
		var parser = new ParserImpl(filter, errorHandler)

		when:
		println(parser.parseProgram().print())

		then:
		noExceptionThrown()

		where:
		file << ["query.txt", "test.txt", "math.txt"]

	}

	def 'Should parse not working program without exceptions'() {
		when:
		Main.main("./src/test/resources/" + file)

		then:
		noExceptionThrown()

		where:
		file << ["error.txt"]
	}
}
