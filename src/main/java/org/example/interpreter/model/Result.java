package org.example.interpreter.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Result {

	Value value;

	@Builder.Default
	boolean present = true;
}
