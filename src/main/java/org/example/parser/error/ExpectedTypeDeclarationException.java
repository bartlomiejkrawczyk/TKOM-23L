package org.example.parser.error;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.token.Token;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@ToString
public class ExpectedTypeDeclarationException extends ParserException {

	Token token;

	public ExpectedTypeDeclarationException(Token token) {
		super(
				String.format(
						"ExpectedTypeDeclaration: %s at position position %s",
						token,
						token.getPosition().toPositionString()
				)
		);
		this.token = token;
	}
}
