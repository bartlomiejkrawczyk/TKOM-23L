package org.example.interpreter.model;

import lombok.Builder;

@Builder
@lombok.Value
public class Result {

	Value value;

	@Builder.Default
	boolean present = true;

	public static Result ok(Value value) {
		return Result.builder()
				.value(value)
				.build();
	}

	public static Result empty() {
		return Result.builder()
				.present(false)
				.build();
	}
}
