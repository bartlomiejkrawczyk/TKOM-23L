package org.example.parser.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Token;

@EqualsAndHashCode(callSuper = true)
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MissingLogicalExpressionException extends CriticalParserException {

	public MissingLogicalExpressionException(Token token) {
		super("Missing Logical Expression", token);
	}
}
