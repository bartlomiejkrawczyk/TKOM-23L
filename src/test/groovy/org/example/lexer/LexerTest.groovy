package org.example.lexer

import org.example.error.ErrorHandler
import org.example.lexer.error.EndOfFileReachedException
import org.example.lexer.error.TokenTooLongException
import org.example.lexer.error.UnexpectedCharacterException
import org.example.lexer.error.UnknownTypeException
import org.example.token.Position
import org.example.token.TokenType
import spock.lang.Specification


class LexerTest extends Specification {

	Lexer toLexer(String content) {
		var reader = new StringReader(content)
		return new LexerImpl(reader)
	}

	def 'Should return correct position in file'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getPosition() == position

		where:
		content             || position
		"123"               || Position.builder().line(0).characterNumber(0).build()
		" 123"              || Position.builder().line(0).characterNumber(1).build()
		"abc"               || Position.builder().line(0).characterNumber(0).build()
		"  abc "            || Position.builder().line(0).characterNumber(2).build()
		"\n1"               || Position.builder().line(1).characterNumber(0).build()
		"\nnewline"         || Position.builder().line(1).characterNumber(0).build()
		"\n\nnewline"       || Position.builder().line(2).characterNumber(0).build()
		" ąĄćĆęĘłŁóÓśŚźŹżŻ" || Position.builder().line(0).characterNumber(1).build()
	}

	def 'Should parse non-keyword identifier correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.IDENTIFIER
		token.getValue() as String == value

		where:
		content              || value
		"abc"                || "abc"
		"abc next"           || "abc"
		"  abc "             || "abc"
		"\nnewline"          || "newline"
		"\n\nnewline"        || "newline"
		" ąĄćĆęĘłŁóÓśŚźŹżŻ"  || "ąĄćĆęĘłŁóÓśŚźŹżŻ"
		" ąĄćĆęĘłŁóÓśŚźŹżŻ " || "ąĄćĆęĘłŁóÓśŚźŹżŻ"
	}

	def 'Should detect correct token type for non-keywords'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == type

		where:
		content || type
		""      || TokenType.END_OF_FILE
		"abc"   || TokenType.IDENTIFIER
		"1"     || TokenType.INTEGER_CONSTANT
		"123"   || TokenType.INTEGER_CONSTANT
		"1.1"   || TokenType.FLOATING_POINT_CONSTANT
		"1.123" || TokenType.FLOATING_POINT_CONSTANT
	}

	def 'Should detect all the keyword tokens correctly'() {
		given:
		var content = type.getKeyword()
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == type

		where:
		type << LexerUtility.KEYWORDS.values()
	}

	def 'Should detect all operators correctly'() {
		given:
		var content = type.getKeyword()
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == type

		where:
		type << LexerUtility.OPERATORS.values()
	}

	def 'Should detect all symbols correctly'() {
		given:
		var content = type.getKeyword()
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == type

		where:
		type << LexerUtility.SYMBOLS.values()
				.stream()
				.filter { it.enclosingKeyword == null }
				.toList()
	}

	def 'Should process single line comments correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.SINGLE_LINE_COMMENT
		token.getValue() as String == value

		where:
		content                                            || value
		"//abc\nnext"                                      || "abc"
		"//W Sczebrzeczynie, chrząszcz brzmi w trzcinie\n" || "W Sczebrzeczynie, chrząszcz brzmi w trzcinie"
	}


	def 'Should process multi line comments correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.MULTI_LINE_COMMENT
		token.getValue() as String == value

		where:
		content                                                 || value
		"/*abc\n*/"                                             || "abc\n"
		"/***/"                                                 || "*"
		"/*\nW Sczebrzeczynie,\nchrząszcz brzmi w trzcinie\n*/" || "\nW Sczebrzeczynie,\nchrząszcz brzmi w trzcinie\n"
	}

	def 'Should parse string constants correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.STRING_DOUBLE_QUOTE_CONSTANT
		token.getValue() as String == value

		where:
		content                            || value
		"\"Ala ma kota\""                  || "Ala ma kota"
		"\"Piszę\nw papierowym zeszycie\"" || "Piszę\nw papierowym zeszycie"
	}

	def 'Should parse integer constants correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.INTEGER_CONSTANT
		token.getValue() as Integer == value

		where:
		content    || value
		"1"        || 1
		"123 next" || 123
	}

	def 'Should parse floating point numbers correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.FLOATING_POINT_CONSTANT
		token.getValue() as Double == value

		where:
		content  || value
		"1.0"    || 1.0
		"1.25"   || 1.25
		"10.750" || 10.750
	}

	def 'Should raise an exception when found symbol is not a operator or comment'() {
		given:
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.getValue() == value
		1 * errorHandler.handleLexerException(_ as UnknownTypeException)

		where:
		content || value
		"@abc"  || "abc"
		"~abc"  || "abc"
	}

	def 'Should raise an exception when floating point number does not have a float part'() {
		given:
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.getValue() == value
		token.getType() == TokenType.FLOATING_POINT_CONSTANT
		1 * errorHandler.handleLexerException(_ as UnexpectedCharacterException)
		lexer.nextToken().getType() == TokenType.END_OF_FILE

		where:
		content || value
		"13."   || 13
		"4."    || 4
		"5."    || 5
		"0."    || 0
	}

	def 'Should raise an exception when identifier is too long'() {
		given:
		var content = "a" * length
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.getType() == TokenType.IDENTIFIER
		1 * errorHandler.handleLexerException(_ as TokenTooLongException)

		where:
		length                                       | _
		LexerConfiguration.MAX_IDENTIFIER_LENGTH + 1 | _
		LexerConfiguration.MAX_IDENTIFIER_LENGTH * 2 | _
	}

	def 'Should raise an exception when comment or string is too long'() {
		given:
		var content = tokenType.getKeyword() + "c" * length + tokenType.getEnclosingKeyword() + " abc"
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.getType() == tokenType
		1 * errorHandler.handleLexerException(_ as TokenTooLongException)
		0 * errorHandler.handleLexerException(_ as EndOfFileReachedException)
		lexer.nextToken().getValue() == "abc"

		where:
		length                                   | tokenType
		LexerConfiguration.MAX_STRING_LENGTH + 1 | TokenType.STRING_DOUBLE_QUOTE_CONSTANT
		LexerConfiguration.MAX_STRING_LENGTH * 2 | TokenType.MULTI_LINE_COMMENT
	}

	def 'Should raise two exceptions when comment or string is too long and not enclosed'() {
		given:
		var content = tokenType.getKeyword() + "c" * length
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.getType() == tokenType
		1 * errorHandler.handleLexerException(_ as TokenTooLongException)
		1 * errorHandler.handleLexerException(_ as EndOfFileReachedException)
		lexer.nextToken().getType() == TokenType.END_OF_FILE

		where:
		length                                   | tokenType
		LexerConfiguration.MAX_STRING_LENGTH + 2 | TokenType.STRING_DOUBLE_QUOTE_CONSTANT
		LexerConfiguration.MAX_STRING_LENGTH * 2 | TokenType.MULTI_LINE_COMMENT
	}

	def 'Should raise an exception when comment or string is not closed'() {
		given:
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.getValue()
		1 * errorHandler.handleLexerException(_ as EndOfFileReachedException)

		where:
		content   || value
		"/*Hello" || "Hello"
		"\"Hello" || "Hello"
	}

	def 'Should raise an exception when lexer finds unexpected character'() {
		given:
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.getType() == TokenType.END_OF_FILE
		2 * errorHandler.handleLexerException(_ as UnexpectedCharacterException)

		where:
		content                                | _
		new String(Character.toChars(0x1F349)) | _
	}
}
