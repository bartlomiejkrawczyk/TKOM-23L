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
public class ReaderException extends LexerException {

	Position position;

	public ReaderException(Position position, String message) {
		super(String.format("Exception occurred while processing token at %s:%n%s", position.toPositionString(), message));
		this.position = position;
	}
}
