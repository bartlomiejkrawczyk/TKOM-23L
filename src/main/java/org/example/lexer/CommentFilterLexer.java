package org.example.lexer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.token.Token;
import org.example.token.TokenType;

@RequiredArgsConstructor
public class CommentFilterLexer implements Lexer {

	private static final List<TokenType> COMMENTS = List.of(
			TokenType.SINGLE_LINE_COMMENT,
			TokenType.MULTI_LINE_COMMENT
	);

	private final Lexer lexer;

	@Override
	public Token nextToken() {
		Token token;
		do {
			token = lexer.nextToken();
		} while (COMMENTS.contains(token.getType()));

		return token;
	}
}
