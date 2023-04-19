package org.example.ast.type;

import org.example.ast.Value;

@lombok.Value
public class StringValue implements Value {

	String value;
}
