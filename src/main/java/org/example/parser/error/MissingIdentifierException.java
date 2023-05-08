package org.example.parser.error;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.token.Token;

@EqualsAndHashCode(callSuper = true)
@ToString
public class MissingIdentifierException extends CriticalParserException {

	public MissingIdentifierException(Token token) {
		super("Missing identifier", token);
	}
}
