package org.example.lexer.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class InconsistentNewLine extends RuntimeException {

	String found;
	String expected;

	public InconsistentNewLine(String found, String expected) {
		this.found = found;
		this.expected = expected;
	}
}
