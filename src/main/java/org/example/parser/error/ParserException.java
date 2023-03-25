package org.example.parser.error;

import java.util.Optional;
import lombok.NoArgsConstructor;
import org.example.error.PositionalException;
import org.example.token.Position;
import org.example.token.Token;

@NoArgsConstructor
public abstract class ParserException extends PositionalException {

	protected ParserException(String message) {
		super(message);
	}

	public abstract Token getToken();

	@Override
	public Position getPosition() {
		return Optional.ofNullable(getToken())
				.map(Token::getPosition)
				.orElse(null);
	}
}
