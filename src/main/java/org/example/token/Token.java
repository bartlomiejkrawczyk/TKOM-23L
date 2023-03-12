package org.example.token;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Token {
	TokenType type;
	Position position;


	Double floatingPointValue;
	Integer integerValue;
	String stringValue;
}
