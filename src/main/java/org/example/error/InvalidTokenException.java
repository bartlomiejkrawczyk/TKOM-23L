package org.example.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Position;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class InvalidTokenException extends RuntimeException {

	Position position;

	public InvalidTokenException(String message, Position position) {
		super(message);
		this.position = position;
	}
}
