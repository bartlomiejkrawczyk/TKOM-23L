package org.example.ast.type;

import org.example.ast.Value;

@lombok.Value
public class FloatingPointValue implements Value {

	Double value;
}
