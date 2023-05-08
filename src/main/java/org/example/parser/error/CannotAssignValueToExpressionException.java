package org.example.parser.error;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.token.Token;

@EqualsAndHashCode(callSuper = true)
@ToString
public class CannotAssignValueToExpressionException extends CriticalParserException {
	public CannotAssignValueToExpressionException(Token token) {
		super("Cannot assign value to expression", token);
	}
}
