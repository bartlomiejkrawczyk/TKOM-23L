package org.example.ast.expression;

import java.util.List;
import lombok.Value;
import org.example.ast.ValueType;

@Value
public class TypeExpression {

	ValueType valueType;
	List<TypeExpression> types;
}
