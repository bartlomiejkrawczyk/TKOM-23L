package org.example.ast.type;

import org.example.ast.Value;

@lombok.Value
public class IntegerValue implements Value {

	Integer value;
}
