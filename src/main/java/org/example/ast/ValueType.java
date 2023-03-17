package org.example.ast;

import java.util.EnumSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum ValueType {
	INTEGER("int"),
	FLOATING_POINT("double"),
	STRING("String"),
	;

	private final String type;

	public static ValueType of(String value) {
		// TODO: Decide what to do when type is not found
		return EnumSet.allOf(ValueType.class)
				.stream()
				.filter(it -> StringUtils.equalsIgnoreCase(it.getType(), value))
				.findFirst()
				.orElse(null);
	}
}
