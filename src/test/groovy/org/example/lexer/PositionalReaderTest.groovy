package org.example.lexer

import org.example.lexer.error.InconsistentNewLine
import org.example.token.Position
import spock.lang.Specification


class PositionalReaderTest extends Specification {

	private static final int END_OF_FILE = -1

	PositionalReader toReader(String content) {
		var stringReader = new StringReader(content)
		return new PositionalReaderImpl(stringReader)
	}

	List<Integer> readAll(PositionalReader reader) {
		var characters = new ArrayList<Integer>()
		var read = reader.read()

		while (read != END_OF_FILE) {
			characters.add(read)
			read = reader.read()
		}
		return characters
	}

	def 'Should convert different new lines to single \\n'() {
		given:
		var reader = toReader(content)

		expect:
		var characters = readAll(reader)

		characters.size() == characterNumber

		where:
		content       | characterNumber
		" "           | 1
		" \n "        | 3
		" \n \n "     | 5
		" \r\n \r\n " | 5
		" \n\r \n\r " | 5
		"Ę\n\rą\n\rŁ" | 5
	}

	def 'Should work with pre defined BufferedReader'() {
		given:
		var stringReader = new StringReader(content)
		var bufferedReader = new BufferedReader(stringReader)
		var reader = new PositionalReaderImpl(bufferedReader)

		expect:
		var characters = readAll(reader)

		characters.size() == characterNumber

		where:
		content       | characterNumber
		" "           | 1
		" \n "        | 3
		" \n \n "     | 5
		" \r\n \r\n " | 5
		" \n\r \n\r " | 5
		"Ę\n\rą\n\rŁ" | 5
	}

	def 'Should throw inconsistent new line when reading different new line characters'() {
		given:
		var reader = toReader(content)

		when:
		readAll(reader)

		then:
		thrown InconsistentNewLine

		where:
		content       | _
		" \n \n\r "   | _
		" \r\n \n "   | _
		" \r\n \n\r " | _
		"Ę\n\rą\r\nŁ" | _
		"Ę\n\rą\nŁ"   | _
	}

	def 'Should detect correct line number and character position'() {
		given:
		var reader = toReader(content)

		expect:
		var characters = readAll(reader)

		reader.getPosition() == position

		where:
		content       || position
		" "           || Position.builder().line(0).characterNumber(1).build()
		""            || Position.builder().line(0).characterNumber(0).build()
		"\n"          || Position.builder().line(1).characterNumber(0).build()
		"\n \n \n"    || Position.builder().line(3).characterNumber(0).build()
		"\n \n \n   " || Position.builder().line(3).characterNumber(3).build()
	}

}
