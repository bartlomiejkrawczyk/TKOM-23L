package org.example.interpreter.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.example.ast.expression.FunctionCallExpression;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ArgumentListSizeDoesNotMatch extends CriticalInterpreterException {

	FunctionCallExpression expression;
}
