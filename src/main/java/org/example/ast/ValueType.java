package org.example.ast;

import java.util.EnumSet;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.token.TokenType;

@Getter
@RequiredArgsConstructor
public enum ValueType {
	INTEGER(TokenType.INT, false),
	FLOATING_POINT(TokenType.FLOATING_POINT_CONSTANT, false),
	STRING(TokenType.STRING, false),
	BOOLEAN(TokenType.BOOLEAN, false),
	MAP(TokenType.MAP, true),
	COMPARATOR(TokenType.COMPARATOR, true),
	ITERABLE(TokenType.ITERABLE, true),
	TUPLE(TokenType.TUPLE, true),
	;

	private final TokenType type;
	private final boolean complex;

	public static Optional<ValueType> of(TokenType provided) {
		return EnumSet.allOf(ValueType.class)
				.stream()
				.filter(it -> it.getType() == provided)
				.findFirst();
	}
}
