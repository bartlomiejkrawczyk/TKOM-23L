package org.example.parser.error;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.token.Token;

@EqualsAndHashCode(callSuper = true)
@ToString
public class MissingTypeDeclaration extends CriticalParserException {

	public MissingTypeDeclaration(Token token) {
		super("Missing type declaration", token);
	}
}
