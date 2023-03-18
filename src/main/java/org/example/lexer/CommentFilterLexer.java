package org.example.lexer;

import lombok.RequiredArgsConstructor;
import org.example.token.Token;

@RequiredArgsConstructor
public class CommentFilterLexer implements Lexer {

	private final Lexer lexer;

	@Override
	public Token nextToken() {
		Token token;
		do {
			token = lexer.nextToken();
		} while (LexerUtility.COMMENTS.containsValue(token.getType()));

		return token;
	}
}
