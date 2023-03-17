package org.example.parser.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Token;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class UnknownTokenException extends RuntimeException {

	Token token;

	public UnknownTokenException(Token token) {
		super(String.format("UnknownToken: %s at position position %s", token, token.getPosition()));
		this.token = token;
	}
}
