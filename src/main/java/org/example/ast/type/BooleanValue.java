package org.example.ast.type;

import org.example.ast.Value;

@lombok.Value
public class BooleanValue implements Value {

	Boolean value;
}
