package org.example.interpreter.model.value;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.model.Value;

@AllArgsConstructor
@Builder(toBuilder = true)
@lombok.Value
public class MapValue implements Value {

	TypeDeclaration type;
	Map<Value, Value> map;
}
