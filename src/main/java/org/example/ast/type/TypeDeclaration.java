package org.example.ast.type;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.example.ast.ValueType;

@AllArgsConstructor
@EqualsAndHashCode
@Value
public class TypeDeclaration {

	ValueType valueType;
	List<TypeDeclaration> types;

	public TypeDeclaration(ValueType valueType) {
		this.valueType = valueType;
		this.types = List.of();
	}
}
