package org.example.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Position;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class TokenTooLongException extends InvalidTokenException {
	String identifier;

	public TokenTooLongException(String identifier, Position position) {
		super(String.format("Token %s... at line %d exceeded maximum size", identifier, position.getLine()), position);
		this.identifier = identifier;
	}
}
