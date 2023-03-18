package org.example.lexer.error;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Position;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class ReaderException extends RuntimeException {

	Position position;

	public ReaderException(Position position, String message) {
		super(String.format("Exception occurred while processing token at %s:%n%s", position, message));
		this.position = position;
	}
}
