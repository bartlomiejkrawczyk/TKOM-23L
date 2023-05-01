package org.example.lexer

import org.example.error.ErrorHandler
import org.example.lexer.error.EndOfFileReachedException
import org.example.lexer.error.NumericOverflowException
import org.example.lexer.error.ReaderException
import org.example.lexer.error.TokenTooLongException
import org.example.lexer.error.UnexpectedCharacterException
import org.example.lexer.error.UnknownTypeException
import org.example.token.Position
import org.example.token.TokenType
import org.example.token.type.IntegerToken
import org.example.token.type.StringToken
import spock.lang.Specification

class LexerTest extends Specification {

	Lexer toLexer(String content) {
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		return new LexerImpl(reader, errorHandler)
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

	def 'Should be able to parse multiple tokens correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		for (def expected : result) {
			var token = lexer.nextToken()
			token == expected
		}

		where:
		content       || result
		"123 234 345" || [new IntegerToken(new Position(0, 0), 123), new IntegerToken(new Position(0, 4), 234), new IntegerToken(new Position(0, 8), 345)]
		"abc bcd cde" || [new StringToken(TokenType.IDENTIFIER, new Position(0, 0), "abc"), new StringToken(TokenType.IDENTIFIER, new Position(0, 4), "bcd"), new StringToken(TokenType.IDENTIFIER, new Position(0, 8), "cde")]
	}

	def 'Should parse non-keyword identifier correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.IDENTIFIER
		token.<String> getValue() == value

		where:
		content              || value
		"abc"                || "abc"
		"abc next"           || "abc"
		"  abc "             || "abc"
		"\nnewline"          || "newline"
		"\n\nnewline"        || "newline"
		" ąĄćĆęĘłŁóÓśŚźŹżŻ"  || "ąĄćĆęĘłŁóÓśŚźŹżŻ"
		" ąĄćĆęĘłŁóÓśŚźŹżŻ " || "ąĄćĆęĘłŁóÓśŚźŹżŻ"
		" Return "           || "Return"
		" wHiLe "            || "wHiLe"
		" FOR "              || "FOR"
		" True "             || "True"
		" FaLsE "            || "FaLsE"
		" forwhile "         || "forwhile"
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

	def 'Should detect all the case insensitive tokens correctly when capitalized'() {
		given:
		var content = type.getKeyword().capitalize()
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == type

		where:
		type << LexerUtility.CASE_INSENSITIVE_KEYWORDS.values()
	}

	def 'Should detect all the case insensitive tokens correctly when uppercase'() {
		given:
		var content = type.getKeyword().toUpperCase()
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == type

		where:
		type << LexerUtility.CASE_INSENSITIVE_KEYWORDS.values()
	}

	def 'Should detect all the case insensitive tokens correctly when lowercase'() {
		given:
		var content = type.getKeyword().toLowerCase()
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == type

		where:
		type << LexerUtility.CASE_INSENSITIVE_KEYWORDS.values()
	}

	def 'Should detect all the boolean tokens correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == type
		token.<Boolean> getValue() == value

		where:
		content   || value | type
		" true "  || true  | TokenType.BOOLEAN_TRUE
		" false " || false | TokenType.BOOLEAN_FALSE
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
		token.<String> getValue() == value

		where:
		content                                           || value
		"#abc\nnext"                                      || "abc"
		"#W Sczebrzeczynie, chrząszcz brzmi w trzcinie\n" || "W Sczebrzeczynie, chrząszcz brzmi w trzcinie"
	}


	def 'Should process multi line comments correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.MULTI_LINE_COMMENT
		token.<String> getValue() == value

		where:
		content                                                 || value
		"/*abc\n*/"                                             || "abc\n"
		"/***/"                                                 || "*"
		"/*\nW Sczebrzeczynie,\nchrząszcz brzmi w trzcinie\n*/" || "\nW Sczebrzeczynie,\nchrząszcz brzmi w trzcinie\n"
	}

	def 'Should parse string constants correctly while surrounded with double quote'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.STRING_DOUBLE_QUOTE_CONSTANT
		token.<String> getValue() == value

		where:
		content                              || value
		"\"Ala ma kota\""                    || "Ala ma kota"
		"\"Piszę\nw papierowym zeszycie\""   || "Piszę\nw papierowym zeszycie"
		"\"Piszę\\nw papierowym\nzeszycie\"" || "Piszę\nw papierowym\nzeszycie"
		"\" \\n \""                          || " \n "
		"\" \\\\ \""                         || " \\ "
		"\" \\\" \""                         || " \" "
		"\" \\\' \""                         || " \' "
		"\" ' \""                            || " ' "
	}

	def 'Should parse string constants correctly while surrounded with single quote'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.STRING_SINGLE_QUOTE_CONSTANT
		token.<String> getValue() == value

		where:
		content                              || value
		"\'Ala ma kota\'"                    || "Ala ma kota"
		"\'Piszę\nw papierowym zeszycie\'"   || "Piszę\nw papierowym zeszycie"
		"\'Piszę\\nw papierowym\nzeszycie\'" || "Piszę\nw papierowym\nzeszycie"
		"\' \\n \'"                          || " \n "
		"\' \\\\ \'"                         || " \\ "
		"\' \\\" \'"                         || " \" "
		"\' \" \'"                           || " \" "
		"\' \\\' \'"                         || " \' "
	}

	def 'Should parse integer constants correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.INTEGER_CONSTANT
		token.<Integer> getValue() == value

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
		token.<Double> getValue() == value

		where:
		content  || value
		"1.0"    || 1.0
		"1.25"   || 1.25
		"10.750" || 10.750
		"0.750" || 0.750
	}

	def 'Should raise an exception when found symbol is not a operator or comment'() {
		given:
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.<String> getValue() == value
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
		token.<Double> getValue() == value
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

	def 'Should raise an exception when integer is too long'() {
		given:
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.<Integer> getValue() == value
		token.getType() == TokenType.INTEGER_CONSTANT
		1 * errorHandler.handleLexerException(_ as NumericOverflowException)

		where:
		content         | value
		"2147483647123" | 2147483647
		"2147483648"    | 214748364
	}

	def 'Should raise an exception when floating point number is too long'() {
		given:
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.<Double> getValue() == value
		token.getType() == TokenType.FLOATING_POINT_CONSTANT
		1 * errorHandler.handleLexerException(_ as NumericOverflowException)

		where:
		content                              | value
		"2147483647.12345678901234567890123" | 2147483647.123456789012345678
		"2147483647.1234567890123456789012"  | 2147483647.123456789012345678
		"2147483647.123456789012345678901"   | 2147483647.123456789012345678
		"2147483647.12345678901234567890"    | 2147483647.123456789012345678
		"2147483647.1234567890123456789"     | 2147483647.123456789012345678
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
		token.<String> getValue().length() == LexerConfiguration.MAX_IDENTIFIER_LENGTH
		1 * errorHandler.handleLexerException(_ as TokenTooLongException)

		where:
		length                                       | _
		LexerConfiguration.MAX_IDENTIFIER_LENGTH | _
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
		token.<String> getValue().length() == LexerConfiguration.MAX_STRING_LENGTH
		1 * errorHandler.handleLexerException(_ as TokenTooLongException)
		0 * errorHandler.handleLexerException(_ as EndOfFileReachedException)
		lexer.nextToken().<String> getValue() == "abc"

		where:
		length                                   | tokenType
		LexerConfiguration.MAX_STRING_LENGTH     | TokenType.STRING_SINGLE_QUOTE_CONSTANT
		LexerConfiguration.MAX_STRING_LENGTH     | TokenType.STRING_DOUBLE_QUOTE_CONSTANT
		LexerConfiguration.MAX_STRING_LENGTH     | TokenType.SINGLE_LINE_COMMENT
		LexerConfiguration.MAX_STRING_LENGTH     | TokenType.MULTI_LINE_COMMENT
		LexerConfiguration.MAX_STRING_LENGTH + 1 | TokenType.STRING_DOUBLE_QUOTE_CONSTANT
		LexerConfiguration.MAX_STRING_LENGTH + 1 | TokenType.MULTI_LINE_COMMENT
		LexerConfiguration.MAX_STRING_LENGTH * 2 | TokenType.SINGLE_LINE_COMMENT
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

	def 'Should raise an exception when string is too long and is escaped'() {
		given:
		var content = tokenType.getKeyword() + "\\" * length + "abc" + tokenType.getKeyword() + " def"
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.getType() == tokenType
		1 * errorHandler.handleLexerException(_ as TokenTooLongException)
		0 * errorHandler.handleLexerException(_ as EndOfFileReachedException)
		lexer.nextToken().<String> getValue() == "def"

		where:
		length                                       | tokenType
		LexerConfiguration.MAX_STRING_LENGTH * 3     | TokenType.STRING_DOUBLE_QUOTE_CONSTANT
		LexerConfiguration.MAX_STRING_LENGTH * 2     | TokenType.STRING_SINGLE_QUOTE_CONSTANT
		LexerConfiguration.MAX_STRING_LENGTH * 2 + 1 | TokenType.STRING_SINGLE_QUOTE_CONSTANT
	}

	def 'Should raise an exception when comment or string is not closed'() {
		given:
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.<String> getValue()
		1 * errorHandler.handleLexerException(_ as EndOfFileReachedException)

		where:
		content   || value
		"/*Hello" || "Hello"
		"\"Hello" || "Hello"
	}

	def 'Should raise an exception when string is not closed and last character is \\'() {
		given:
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		token.<String> getValue()
		2 * errorHandler.handleLexerException(_ as EndOfFileReachedException)

		where:
		content     || value
		"\"Hello\\" || "Hello\\"
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

	def 'Should raise an exception when reader throws an IOException'() {
		given:
		var reader = Mock(BufferedReader)
		reader.read() >> { throw new IOException() }
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)

		when:
		var token = lexer.nextToken()

		then:
		1 * errorHandler.handleLexerException(_ as ReaderException)
		token.getType() == TokenType.END_OF_FILE
		noExceptionThrown()
	}
}
