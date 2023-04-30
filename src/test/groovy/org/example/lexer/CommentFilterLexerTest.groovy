package org.example.lexer

import org.example.token.Position
import org.example.token.TokenType
import org.example.token.type.StringToken
import spock.lang.Specification
import spock.lang.Subject


class CommentFilterLexerTest extends Specification {

	var position = Position.builder()
			.line(0)
			.characterNumber(0)
			.build()

	var comment = "Comment"

	def 'Should filter comments correctly'() {
		given:
		var lexer = Mock(Lexer)

		@Subject
		var filter = new CommentFilterLexer(lexer)

		1 * lexer.nextToken() >> new StringToken(tokenType, position, comment)
		1 * lexer.nextToken() >> new StringToken(TokenType.IDENTIFIER, position, comment)

		expect:
		filter.nextToken().getType() == TokenType.IDENTIFIER

		where:
		tokenType << LexerUtility.COMMENTS.values()
	}
}
