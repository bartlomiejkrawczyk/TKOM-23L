package org.example.ast.type;

import java.util.List;
import lombok.Value;
import org.example.ast.ValueType;

@Value
public class TypeDeclaration {

	ValueType valueType;
	List<TypeDeclaration> types;
}
