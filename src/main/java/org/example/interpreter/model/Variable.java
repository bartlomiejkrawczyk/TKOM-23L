package org.example.interpreter.model;

import lombok.Value;

@Value
public class Variable {

	String identifier;
	String string;
	Integer integer;
	Double floatingPoint;
}
