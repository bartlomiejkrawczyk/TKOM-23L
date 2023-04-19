package org.example.ast.type;

import java.util.Map;
import org.example.ast.Value;

@lombok.Value
public class MapValue<K, V> implements Value {

	Map<K, V> value;
}
