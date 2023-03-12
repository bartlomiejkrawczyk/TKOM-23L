package org.example.lexer

import org.example.token.Position
import org.example.token.TokenType
import org.junit.platform.commons.util.StringUtils
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
		token.getStringValue() == value

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
		type << EnumSet.allOf(TokenType.class)
				.stream()
				.filter { StringUtils.isNotBlank(it.getKeyword()) }
				.filter { it != TokenType.SINGLE_LINE_COMMENT }
				.filter { it != TokenType.MULTI_LINE_COMMENT }
				.toList()
	}

	def 'Should process single line comments correctly'() {
		given:
		var lexer = toLexer(content)

		expect:
		var token = lexer.nextToken()
		token.getType() == TokenType.SINGLE_LINE_COMMENT
		token.getStringValue() == value

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
		token.getStringValue() == value

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
		token.getType() == TokenType.STRING_CONSTANT
		token.getStringValue() == value

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
		token.getNumericalValue() == BigDecimal.valueOf(value)

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
		token.getNumericalValue() == BigDecimal.valueOf(value)

		where:
		content  || value
		"1.0"    || 1.0
		"1.25"   || 1.25
		"10.750" || 10.750
	}
}
