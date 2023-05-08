package org.example.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Position implements Comparable<Position> {

	private static final int BEFORE_FIRST_LETTER = 0;

	@Getter
	int line;
	int characterNumber;

	public Position() {
		this.line = 1;
		this.characterNumber = BEFORE_FIRST_LETTER;
	}

	public void nextCharacter() {
		characterNumber++;
	}

	public void nextLine() {
		characterNumber = BEFORE_FIRST_LETTER;
		line++;
	}

	public Position copy() {
		return this.toBuilder().build();
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
		return "line " + line + ", character " + characterNumber;
	}
}
