package org.example.interpreter.model;


@lombok.Value
public class Variable implements Value {

	String identifier;
	String string;
	Integer integer;
	Boolean bool;
	Double floatingPoint;
}
