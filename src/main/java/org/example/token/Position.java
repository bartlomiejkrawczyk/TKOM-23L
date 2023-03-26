package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Builder(toBuilder = true)
@AllArgsConstructor
@Value
public class Position implements Comparable<Position> {

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

	@Override
	public int compareTo(Position position) {
		if (this.line > position.line || this.characterNumber > position.characterNumber) {
			return 1;
		} else if (this.line == position.line && this.characterNumber == position.characterNumber) {
			return 0;
		} else {
			return -1;
		}
	}

	public String toPositionString() {
		return "line " + (line + 1) + ", character " + (characterNumber + 1);
	}
}
