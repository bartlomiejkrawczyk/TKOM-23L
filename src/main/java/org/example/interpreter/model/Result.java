package org.example.interpreter.model;

import lombok.Builder;

@Builder(toBuilder = true)
@lombok.Value
public class Result {

	Value value;

	@Builder.Default
	boolean present = true;

	@Builder.Default
	boolean returned = false;

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
