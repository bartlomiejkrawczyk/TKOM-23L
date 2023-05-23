package org.example.interpreter;

import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.example.ast.ValueType;
import org.example.ast.expression.Argument;
import org.example.ast.expression.BlockStatement;
import org.example.ast.statement.FunctionDefinitionStatement;
import org.example.ast.type.TypeDeclaration;
import org.example.interpreter.model.Context;
import org.example.interpreter.model.custom.PrintFunction;
import org.example.token.Position;

@UtilityClass
public class InterpreterUtility {

	public static final Position DEFAULT_POSITION = new Position(1, 1);
	public static final Context GLOBAL_CONTEXT = new Context("~~main~~", DEFAULT_POSITION);
	public static final TypeDeclaration VOID_TYPE = new TypeDeclaration(ValueType.VOID);
	public static final TypeDeclaration BOOLEAN_TYPE = new TypeDeclaration(ValueType.BOOLEAN);
	public static final TypeDeclaration INTEGER_TYPE = new TypeDeclaration(ValueType.INTEGER);
	public static final TypeDeclaration FLOATING_POINT_TYPE = new TypeDeclaration(ValueType.FLOATING_POINT);
	public static final TypeDeclaration STRING_TYPE = new TypeDeclaration(ValueType.STRING);
	public static final String PRINT_ARGUMENT = "~~message~~";
	public static final String MAIN_FUNCTION_NAME = "main";
	public static final Map<String, FunctionDefinitionStatement> BUILTIN_FUNCTIONS = Map.of(
			"print",
			new FunctionDefinitionStatement(
					"print",
					List.of(new Argument(PRINT_ARGUMENT, new TypeDeclaration(ValueType.STRING))),
					new TypeDeclaration(ValueType.VOID),
					new BlockStatement(List.of(new PrintFunction()), DEFAULT_POSITION),
					DEFAULT_POSITION
			)
	);
}
