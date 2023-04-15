package org.example.lexer.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Position;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@ToString
public class EndOfFileReachedException extends LexerException {

	String expectedEnclosingKeyword;
	Position position;

	public EndOfFileReachedException(String expectedEnclosingKeyword, Position position) {
		super(String.format("String starting at %s is missing a closing character combination: %s", position.toPositionString(),
				expectedEnclosingKeyword));
		this.expectedEnclosingKeyword = expectedEnclosingKeyword;
		this.position = position;
	}

}
