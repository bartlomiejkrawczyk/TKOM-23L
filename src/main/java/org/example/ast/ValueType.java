package org.example.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ValueType {
	INTEGER("int"),
	FLOATING_POINT("double"),
	STRING("String"),
	;

	private final String type;
}
