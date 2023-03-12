package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Builder(toBuilder = true)
@AllArgsConstructor
@Value
public class Position {

	private static final int BEFORE_FIRST_LETTER = -1;

	int line;
	int characterNumber;

	public Position() {
		this.line = 0;
		this.characterNumber = BEFORE_FIRST_LETTER;
	}

	public Position nextCharacter() {
		return toBuilder()
				.characterNumber(characterNumber + 1)
				.build();
	}

	public Position nextLine() {
		return Position.builder()
				.characterNumber(BEFORE_FIRST_LETTER)
				.line(line + 1)
				.build();
	}
}
