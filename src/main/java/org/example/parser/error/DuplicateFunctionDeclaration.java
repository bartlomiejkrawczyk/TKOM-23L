package org.example.parser.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Token;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DuplicateFunctionDeclaration extends CriticalParserException {

	String identifier;

	public DuplicateFunctionDeclaration(Token token, String identifier) {
		super("Redeclaration of a function", token);
		this.identifier = identifier;
	}
}
