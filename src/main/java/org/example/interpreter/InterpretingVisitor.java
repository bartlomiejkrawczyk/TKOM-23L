package org.example.interpreter;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ast.Program;
import org.example.ast.ValueType;
import org.example.ast.expression.Argument;
import org.example.ast.expression.BlockStatement;
import org.example.ast.expression.ExplicitCastExpression;
import org.example.ast.expression.FunctionCallExpression;
import org.example.ast.expression.IdentifierExpression;
import org.example.ast.expression.MapExpression;
import org.example.ast.expression.MethodCallExpression;
import org.example.ast.expression.SelectExpression;
import org.example.ast.expression.TupleCallExpression;
import org.example.ast.expression.TupleExpression;
import org.example.ast.expression.arithmetic.BinaryArithmeticExpression;
import org.example.ast.expression.arithmetic.NegationArithmeticExpression;
import org.example.ast.expression.logical.BinaryLogicalExpression;
import org.example.ast.expression.logical.NegateLogicalExpression;
import org.example.ast.expression.relation.RelationLogicalExpression;
import org.example.ast.statement.AssignmentStatement;
import org.example.ast.statement.DeclarationStatement;
import org.example.ast.statement.ForStatement;
import org.example.ast.statement.FunctionDefinitionStatement;
import org.example.ast.statement.IfStatement;
import org.example.ast.statement.ReturnStatement;
import org.example.ast.statement.WhileStatement;
import org.example.ast.type.BooleanExpression;
import org.example.ast.type.FloatingPointExpression;
import org.example.ast.type.IntegerExpression;
import org.example.ast.type.StringExpression;
import org.example.ast.type.TupleElement;
import org.example.ast.type.TypeDeclaration;
import org.example.error.ErrorHandler;
import org.example.interpreter.error.ArgumentListSizeDoesNotMatch;
import org.example.interpreter.error.CouldNotCompareNonNumericValues;
import org.example.interpreter.error.CouldNotPerformArithmeticOperationOnNonNumericType;
import org.example.interpreter.error.CriticalInterpreterException;
import org.example.interpreter.error.ExpressionDidNotEvaluateException;
import org.example.interpreter.error.NoSuchFunctionException;
import org.example.interpreter.error.NoSuchVariableException;
import org.example.interpreter.error.ReturnCalled;
import org.example.interpreter.error.ReturnValueNotExpectedException;
import org.example.interpreter.error.TypesDoNotMatchException;
import org.example.interpreter.error.UnsupportedCastException;
import org.example.interpreter.model.Context;
import org.example.interpreter.model.Result;
import org.example.interpreter.model.Value;
import org.example.interpreter.model.Variable;
import org.example.interpreter.model.custom.PrintFunction;
import org.example.interpreter.model.value.BooleanValue;
import org.example.interpreter.model.value.FloatingPointValue;
import org.example.interpreter.model.value.IntegerValue;
import org.example.interpreter.model.value.StringValue;
import org.example.token.Position;

@Slf4j
@RequiredArgsConstructor
public class InterpretingVisitor implements Visitor, Interpreter {

	private static final Context GLOBAL_CONTEXT = new Context(new TypeDeclaration(ValueType.VOID), "~~main~~");
	private static final TypeDeclaration VOID_TYPE = new TypeDeclaration(ValueType.VOID);
	private static final TypeDeclaration BOOLEAN_TYPE = new TypeDeclaration(ValueType.BOOLEAN);
	private static final TypeDeclaration INTEGER_TYPE = new TypeDeclaration(ValueType.INTEGER);
	private static final TypeDeclaration FLOATING_POINT_TYPE = new TypeDeclaration(ValueType.FLOATING_POINT);
	private static final TypeDeclaration STRING_TYPE = new TypeDeclaration(ValueType.STRING);
	private static final String PRINT_ARGUMENT = "~~message~~";
	private static final String MAIN_FUNCTION_NAME = "main";
	private static final String NOT_IMPLEMENTED_YET = "Not implemented yet!";
	private final ErrorHandler errorHandler;
	private final PrintStream out;
	private final Deque<Context> contexts = new ArrayDeque<>(List.of(GLOBAL_CONTEXT));
	private final Map<String, FunctionDefinitionStatement> functionDefinitions = new HashMap<>(Map.of(
			"print",
			new FunctionDefinitionStatement(
					"print",
					List.of(new Argument(PRINT_ARGUMENT, new TypeDeclaration(ValueType.STRING))),
					new TypeDeclaration(ValueType.VOID),
					new BlockStatement(List.of(new PrintFunction()), Position.builder().build()),
					Position.builder().build()
			)
	));

	private Result result = Result.empty();

	@Override
	public void execute(Program program) {
		try {
			program.accept(this);
		} catch (CriticalInterpreterException exception) {
			for (var context : contexts) {
				log.error(context.getFunction());
			}
			errorHandler.handleInterpreterException(exception);
			log.error("TODO: delete me!", exception);
		}
	}

	@Override
	public void visit(Program program) {
		functionDefinitions.putAll(program.getFunctionDefinitions());

		for (var declaration : program.getDeclarations().values()) {
			declaration.accept(this);
		}

		var main = new FunctionCallExpression(MAIN_FUNCTION_NAME, List.of(), program.getPosition());
		main.accept(this);
	}

	@Override
	public void visit(FunctionDefinitionStatement statement) {
		// TODO: might not evaluate this ???
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(DeclarationStatement statement) {
		var context = contexts.getLast();
		var argument = statement.getArgument();

		statement.getExpression().accept(this);
		var value = retrieveResult(argument.getType());

		var variable = new Variable(argument.getType(), argument.getName(), value);
		context.addVariable(variable);
	}

	@Override
	public void visit(IfStatement statement) {
		statement.getCondition().accept(this);
		var value = retrieveResult(BOOLEAN_TYPE);

		if (value.getBool()) {
			statement.getIfTrue().accept(this);
		} else {
			statement.getIfFalse().accept(this);
		}
	}

	@Override
	public void visit(WhileStatement statement) {
		var condition = statement.getCondition();
		condition.accept(this);
		var value = retrieveResult(BOOLEAN_TYPE);

		while (value.getBool()) {
			statement.getBody().accept(this);

			condition.accept(this);
			value = retrieveResult(BOOLEAN_TYPE);
		}
	}

	@Override
	public void visit(ForStatement statement) {
		// TODO: implement me!
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(AssignmentStatement statement) {
		var context = contexts.getLast();
		var previousValue = context.findVariable(statement.getName())
				.or(() -> GLOBAL_CONTEXT.findVariable(statement.getName()))
				.orElseThrow(NoSuchVariableException::new);

		statement.getValue().accept(this);
		var value = retrieveResult(previousValue.getType());

		for (var current : List.of(context, GLOBAL_CONTEXT)) {
			var updated = current.updateVariable(statement.getName(), value);
			if (updated) {
				return;
			}
		}
	}

	@Override
	public void visit(ReturnStatement statement) {
		statement.getExpression().accept(this);
		throw new ReturnCalled();
	}

	@Override
	public void visit(BlockStatement block) {
		var context = contexts.getLast();
		context.incrementScope();

		for (var statement : block.getStatements()) {
			statement.accept(this);
		}

		context.decrementScope();
	}

	@Override
	public void visit(BinaryLogicalExpression expression) {
		expression.getLeft().accept(this);
		var left = retrieveResult(BOOLEAN_TYPE);

		if (expression.needsFurtherProcessing(left.getBool())) {
			expression.getRight().accept(this);
			var right = retrieveResult(BOOLEAN_TYPE);
			result = Result.ok(new BooleanValue(expression.evaluate(left.getBool(), right.getBool())));
		} else {
			result = Result.ok(new BooleanValue(left.getBool()));
		}
	}

	@Override
	public void visit(NegateLogicalExpression expression) {
		expression.getExpression().accept(this);
		var value = retrieveResult(BOOLEAN_TYPE);
		result = Result.ok(new BooleanValue(!value.getBool()));
	}

	@Override
	public void visit(BooleanExpression value) {
		result = Result.ok(new BooleanValue(value.getValue()));
	}

	@Override
	public void visit(RelationLogicalExpression expression) {
		expression.getLeft().accept(this);
		var left = retrieveResult();

		boolean value;
		if (Objects.equals(left.getType(), INTEGER_TYPE)) {
			var right = retrieveResult(left.getType());
			value = expression.evaluate(left.getInteger(), right.getInteger());
		} else if (Objects.equals(left.getType(), FLOATING_POINT_TYPE)) {
			var right = retrieveResult(left.getType());
			value = expression.evaluate(left.getFloatingPoint(), right.getFloatingPoint());
		} else {
			throw new CouldNotCompareNonNumericValues();
		}
		result = Result.ok(new BooleanValue(value));
	}

	@Override
	public void visit(BinaryArithmeticExpression expression) {
		expression.getLeft().accept(this);
		var left = retrieveResult();

		if (Objects.equals(left.getType(), INTEGER_TYPE)) {
			expression.getRight().accept(this);
			var right = retrieveResult(left.getType());
			var value = expression.evaluate(left.getInteger(), right.getInteger());
			result = Result.ok(new IntegerValue(value));
		} else if (Objects.equals(left.getType(), FLOATING_POINT_TYPE)) {
			expression.getRight().accept(this);
			var right = retrieveResult(left.getType());
			var value = expression.evaluate(left.getFloatingPoint(), right.getFloatingPoint());
			result = Result.ok(new FloatingPointValue(value));
		} else {
			throw new CouldNotPerformArithmeticOperationOnNonNumericType();
		}
	}

	@Override
	public void visit(NegationArithmeticExpression expression) {
		expression.getExpression().accept(this);
		var value = retrieveResult();

		if (Objects.equals(value.getType(), INTEGER_TYPE)) {
			result = Result.ok(new IntegerValue(-value.getInteger()));
		} else if (Objects.equals(value.getType(), FLOATING_POINT_TYPE)) {
			result = Result.ok(new FloatingPointValue(-value.getFloatingPoint()));
		} else {
			throw new CouldNotPerformArithmeticOperationOnNonNumericType();
		}
	}

	@Override
	public void visit(IntegerExpression expression) {
		result = Result.ok(new IntegerValue(expression.getValue()));
	}

	@Override
	public void visit(FloatingPointExpression expression) {
		result = Result.ok(new FloatingPointValue(expression.getValue()));
	}

	@Override
	public void visit(StringExpression expression) {
		result = Result.ok(new StringValue(expression.getValue()));
	}

	@Override
	public void visit(TupleCallExpression expression) {
		// TODO: implement me!
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(MethodCallExpression expression) {
		// TODO: implement me!
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(IdentifierExpression expression) {
		var context = contexts.getLast();
		var variable = context.findVariable(expression.getName())
				.or(() -> GLOBAL_CONTEXT.findVariable(expression.getName()))
				.orElseThrow(NoSuchVariableException::new);
		result = Result.ok(variable.getValue());
	}

	@Override
	public void visit(FunctionCallExpression expression) {
		if (!functionDefinitions.containsKey(expression.getFunction())) {
			throw new NoSuchFunctionException(expression.getFunction());
		}
		var declaration = functionDefinitions.get(expression.getFunction());

		var arguments = expression.getArguments();

		if (declaration.getArguments().size() != arguments.size()) {
			throw new ArgumentListSizeDoesNotMatch(expression);
		}

		var context = new Context(declaration.getReturnType(), declaration.getName());

		for (int i = 0; i < arguments.size(); i++) {
			arguments.get(i).accept(this);
			var argument = declaration.getArguments().get(i);
			var variable = retrieveResult(argument.getType());
			context.addVariable(new Variable(argument.getType(), argument.getName(), variable));
		}

		contexts.add(context);

		try {
			declaration.getBody().accept(this);
			if (Objects.equals(declaration.getReturnType(), VOID_TYPE)) {
				result = Result.empty();
			} else {
				throw new ReturnValueNotExpectedException();
			}
		} catch (ReturnCalled exception) {
			if (!Objects.equals(result.getValue().getType(), declaration.getReturnType())) {
				throw new TypesDoNotMatchException(result.getValue().getType(), declaration.getReturnType());
			}
		}

		contexts.removeLast();
	}

	@Override
	public void visit(SelectExpression expression) {
		// TODO: implement me!
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(TupleExpression expression) {
		// TODO: implement me!
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(TupleElement expression) {
		// TODO: might not use this
	}

	@Override
	public void visit(MapExpression expression) {
		// TODO: implement me!
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	private static final Map<TypeDeclaration, Map<TypeDeclaration, Function<Value, Value>>> explicitCastConverters = Map.of(
			INTEGER_TYPE, Map.of(
					INTEGER_TYPE, Function.identity(),
					FLOATING_POINT_TYPE, it -> new IntegerValue((int) it.getFloatingPoint()),
					BOOLEAN_TYPE, it -> new IntegerValue(it.getBool() ? 1 : 0)
			),
			FLOATING_POINT_TYPE, Map.of(
					INTEGER_TYPE, it -> new FloatingPointValue(it.getInteger()),
					FLOATING_POINT_TYPE, Function.identity(),
					BOOLEAN_TYPE, it -> new FloatingPointValue(it.getBool() ? 1.0D : 0.0D)
			),
			BOOLEAN_TYPE, Map.of(
					INTEGER_TYPE, it -> new BooleanValue(it.getInteger() != 0),
					FLOATING_POINT_TYPE, it -> new BooleanValue(it.getFloatingPoint() != 0),
					BOOLEAN_TYPE, Function.identity()
			),
			STRING_TYPE, Map.of(
					INTEGER_TYPE, it -> new StringValue(String.valueOf(it.getInteger())),
					FLOATING_POINT_TYPE, it -> new StringValue(String.valueOf(it.getFloatingPoint())),
					BOOLEAN_TYPE, it -> new StringValue(String.valueOf(it.getBool())),
					STRING_TYPE, Function.identity()
					// TODO: possibly implement other types too
			)
	);

	@Override
	public void visit(ExplicitCastExpression expression) {
		var type = expression.getType();
		if (!explicitCastConverters.containsKey(type)) {
			throw new UnsupportedCastException();
		}
		var converters = explicitCastConverters.get(type);

		var supportedTypes = converters.keySet();

		expression.getExpression().accept(this);
		var toCast = retrieveResult();

		if (!supportedTypes.contains(toCast.getType())) {
			throw new UnsupportedCastException();
		}

		var value = converters.get(toCast.getType()).apply(toCast);
		result = Result.ok(value);
	}

	@Override
	@SuppressWarnings("java:S106")
	public void visit(PrintFunction expression) {
		var context = contexts.getLast();

		var message = context.findVariable(PRINT_ARGUMENT)
				.orElseThrow(NoSuchVariableException::new);

		if (!Objects.equals(message.getType(), STRING_TYPE)) {
			throw new TypesDoNotMatchException(message.getType(), STRING_TYPE);
		}

		var value = message.getValue();
		out.println(value.getString());
	}

	private Value retrieveResult(TypeDeclaration type) {
		var value = retrieveResult();

		if (!Objects.equals(value.getType(), type)) {
			throw new TypesDoNotMatchException(value.getType(), type);
		}

		return value;
	}

	private Value retrieveResult() {
		if (!result.isPresent()) {
			throw new ExpressionDidNotEvaluateException();
		}

		return result.getValue();
	}
}
