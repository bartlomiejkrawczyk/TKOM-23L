package org.example.parser.error;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.token.Token;

@EqualsAndHashCode(callSuper = true)
@ToString
public class MissingArgumentException extends CriticalParserException {

	public MissingArgumentException(Token token) {
		super("Missing argument", token);
	}
}
