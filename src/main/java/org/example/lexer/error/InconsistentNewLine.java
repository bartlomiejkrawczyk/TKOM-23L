package org.example.lexer.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Position;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class InconsistentNewLine extends LexerException {

	String found;
	String expected;
	Position position;

	public InconsistentNewLine(String found, String expected, Position position) {
		this.found = found;
		this.expected = expected;
		this.position = position;
	}
}
