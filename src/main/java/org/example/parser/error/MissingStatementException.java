package org.example.parser.error;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.token.Token;

@EqualsAndHashCode(callSuper = true)
@ToString
public class MissingStatementException extends CriticalParserException {

	public MissingStatementException(Token token) {
		super("Missing statement", token);
	}
}
