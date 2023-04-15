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
public class InvalidTokenException extends LexerException {

	Position position;

	public InvalidTokenException(String message, Position position) {
		super(message);
		this.position = position;
	}
}
