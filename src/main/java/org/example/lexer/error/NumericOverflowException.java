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
public class NumericOverflowException extends LexerException {

	Position position;

	public NumericOverflowException(Position position) {
		super(String.format("Number at %s has reached limit", position.toPositionString()));
		this.position = position;
	}
}
