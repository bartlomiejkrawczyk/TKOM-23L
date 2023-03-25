package org.example.lexer.error;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Position;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class NumericOverflowException extends LexerException {

	Position position;

	public NumericOverflowException(Position position) {
		super(String.format("Number at %s has reached limit", position));
		this.position = position;
	}
}
