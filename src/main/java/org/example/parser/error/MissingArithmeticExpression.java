package org.example.parser.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Token;

@EqualsAndHashCode(callSuper = true)
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MissingArithmeticExpression extends CriticalParserException {

	public MissingArithmeticExpression(Token token) {
		super("Missing Arithmetic Expression", token);
	}
}
