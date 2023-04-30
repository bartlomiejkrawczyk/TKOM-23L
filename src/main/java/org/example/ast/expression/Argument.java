package org.example.ast.expression;

import lombok.Value;
import org.example.ast.type.TypeDeclaration;

@Value
public class Argument {

	String name;
	TypeDeclaration type;
}
