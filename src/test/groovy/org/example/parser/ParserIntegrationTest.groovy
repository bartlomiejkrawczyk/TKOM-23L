package org.example.parser

import org.example.ast.ValueType
import org.example.ast.expression.BlockExpression
import org.example.ast.expression.FunctionDefinitionExpression
import org.example.ast.expression.Program
import org.example.ast.type.TypeDeclaration
import org.example.error.ErrorHandler
import org.example.lexer.LexerImpl
import spock.lang.Specification


class ParserIntegrationTest extends Specification {

	Parser toParser(String content) {
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)
		return new ParserImpl(lexer, errorHandler)
	}

	def 'Should be able to parse empty program'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program || result
		" "     || new Program(Map.of(), List.of())
	}

	def 'Should be able to parse basic function definition'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program         || result
		"fun main() {}" || new Program(Map.of("main", new FunctionDefinitionExpression("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of()))), List.of())
	}
}
