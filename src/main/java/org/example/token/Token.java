package org.example.token;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Token {
	TokenType type;
	Position position;

	BigDecimal numericalValue;
	String stringValue;
}
