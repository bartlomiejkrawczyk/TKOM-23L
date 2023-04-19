package org.example.ast.type;

import java.util.Map;
import org.example.ast.Value;

@lombok.Value
public class TupleValue implements Value {

	Map<String, TupleElement> value;
}
