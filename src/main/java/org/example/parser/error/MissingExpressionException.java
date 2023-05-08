package org.example.parser.error;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.token.Token;

@EqualsAndHashCode(callSuper = true)
@ToString
public class MissingExpressionException extends CriticalParserException {

	public MissingExpressionException(Token token) {
		super("Missing expression", token);
	}
}
