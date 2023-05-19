package org.example.parser.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Token;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DuplicateDeclaration extends CriticalParserException {

	String identifier;

	public DuplicateDeclaration(Token token, String identifier) {
		super("Redeclaration of a variable", token);
		this.identifier = identifier;
	}
}
