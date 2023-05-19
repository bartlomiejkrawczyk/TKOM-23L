package org.example.interpreter.model;

import lombok.Builder;

@Builder
@lombok.Value
public class Result implements Value {

	Double floatingPoint;
	Integer integer;
	String string;
	Boolean bool;

	@Builder.Default
	boolean present = true;
}
