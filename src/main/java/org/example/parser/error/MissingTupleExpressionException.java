package org.example.parser.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Token;

@EqualsAndHashCode(callSuper = true)
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MissingTupleExpressionException extends CriticalParserException {

	public MissingTupleExpressionException(Token token) {
		super("Missing tuple expression", token);
	}
}
