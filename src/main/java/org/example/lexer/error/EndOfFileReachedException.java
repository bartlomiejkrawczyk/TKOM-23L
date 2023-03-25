package org.example.lexer.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Position;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class EndOfFileReachedException extends LexerException {

	String expectedEnclosingKeyword;
	Position position;

	public EndOfFileReachedException(String expectedEnclosingKeyword, Position position) {
		super(String.format("String starting at %s is missing a closing character combination: %s", position, expectedEnclosingKeyword));
		this.expectedEnclosingKeyword = expectedEnclosingKeyword;
		this.position = position;
	}

}
