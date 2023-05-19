package org.example.interpreter.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Result {

	Double floatingPoint;
	Integer integer;
	String string;

	@Builder.Default
	boolean present = true;
}
