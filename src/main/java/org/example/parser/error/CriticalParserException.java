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
public class CriticalParserException extends ParserException {

	Token token;

	public CriticalParserException(String message, Token token) {
		super(
				String.format(
						"CriticalParserException: for token %s at position %s%nMessage: %s",
						token,
						token.getPosition(),
						message
				)
		);
		this.token = token;
	}
}
