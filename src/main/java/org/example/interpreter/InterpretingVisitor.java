package org.example.interpreter;

import static org.example.interpreter.InterpreterConfiguration.MAX_STACK_SIZE;
import static org.example.interpreter.InterpreterUtility.BOOLEAN_TYPE;
import static org.example.interpreter.InterpreterUtility.BUILTIN_FUNCTIONS;
import static org.example.interpreter.InterpreterUtility.DEFAULT_POSITION;
import static org.example.interpreter.InterpreterUtility.FLOATING_POINT_TYPE;
import static org.example.interpreter.InterpreterUtility.GLOBAL_CONTEXT;
import static org.example.interpreter.InterpreterUtility.INTEGER_TYPE;
import static org.example.interpreter.InterpreterUtility.MAIN_FUNCTION_NAME;
import static org.example.interpreter.InterpreterUtility.PRINT_ARGUMENT;
import static org.example.interpreter.InterpreterUtility.STRING_TYPE;
import static org.example.interpreter.InterpreterUtility.VOID_TYPE;

import io.vavr.Tuple3;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.example.ast.Expression;
import org.example.ast.Node;
import org.example.ast.Program;
import org.example.ast.ValueType;
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
import org.example.ast.expression.relation.EqualityRelationLogicalExpression;
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
import org.example.interpreter.error.MaxFunctionStackSizeReachedException;
import org.example.interpreter.error.NoSuchFunctionException;
import org.example.interpreter.error.NoSuchTupleElement;
import org.example.interpreter.error.NoSuchVariableException;
import org.example.interpreter.error.ObjectDoesNotSupportMethodCallsException;
import org.example.interpreter.error.ReturnCalled;
import org.example.interpreter.error.ReturnValueExpectedException;
import org.example.interpreter.error.TypesDoNotMatchException;
import org.example.interpreter.error.UnsupportedCastException;
import org.example.interpreter.error.UnsupportedOperationException;
import org.example.interpreter.model.Context;
import org.example.interpreter.model.Result;
import org.example.interpreter.model.Value;
import org.example.interpreter.model.Variable;
import org.example.interpreter.model.custom.PrintFunction;
import org.example.interpreter.model.value.BooleanValue;
import org.example.interpreter.model.value.ComparatorValue;
import org.example.interpreter.model.value.FloatingPointValue;
import org.example.interpreter.model.value.IntegerValue;
import org.example.interpreter.model.value.IterableValue;
import org.example.interpreter.model.value.MapValue;
import org.example.interpreter.model.value.StringValue;
import org.example.interpreter.model.value.TupleValue;
import org.example.token.Position;

@Slf4j
@RequiredArgsConstructor
public class InterpretingVisitor implements Visitor, Interpreter {

	private final ErrorHandler errorHandler;
	private final PrintStream out;
	private final Program program;

	private final Deque<Context> contexts = new ArrayDeque<>(List.of(GLOBAL_CONTEXT));
	private final Map<String, FunctionDefinitionStatement> functionDefinitions = new HashMap<>(BUILTIN_FUNCTIONS);
	private Position currentPosition = DEFAULT_POSITION;
	private Result result = Result.empty();

	@Override
	public void execute() {
		try {
			callAccept(program);
		} catch (CriticalInterpreterException exception) {
			exception.setPosition(currentPosition);
			var stack = contexts.stream()
					.map(it -> Pair.of(it.getFunction(), it.getPosition()))
					.map(it -> String.format("%s: %s", it.getKey(), it.getValue()))
					.toList();
			exception.setContextStack(stack);
			errorHandler.handleInterpreterException(exception);
		}
	}

	@Override
	public void visit(Program program) {
		functionDefinitions.putAll(program.getFunctionDefinitions());

		for (var declaration : program.getDeclarations().values()) {
			callAccept(declaration);
		}

		var main = new FunctionCallExpression(MAIN_FUNCTION_NAME, List.of(), program.getPosition());
		callAccept(main);
	}

	@Override
	public void visit(FunctionDefinitionStatement statement) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(DeclarationStatement statement) {
		var context = contexts.getLast();
		var argument = statement.getArgument();

		callAccept(statement.getExpression());
		var value = retrieveResult(argument.getType());

		var variable = new Variable(argument.getType(), argument.getName(), value);
		context.addVariable(variable);
	}

	@Override
	public void visit(IfStatement statement) {
		callAccept(statement.getCondition());
		var value = retrieveResult(BOOLEAN_TYPE);

		if (value.isBool()) {
			callAccept(statement.getIfTrue());
		} else {
			callAccept(statement.getIfFalse());
		}
	}

	@Override
	public void visit(WhileStatement statement) {
		var condition = statement.getCondition();
		callAccept(condition);
		var value = retrieveResult(BOOLEAN_TYPE);

		while (value.isBool()) {
			callAccept(statement.getBody());

			callAccept(condition);
			value = retrieveResult(BOOLEAN_TYPE);
		}
	}

	@Override
	public void visit(ForStatement statement) {
		var context = contexts.getLast();

		var argument = statement.getArgument();
		var type = argument.getType();


		callAccept(statement.getIterable());
		var value = retrieveResult(new TypeDeclaration(ValueType.ITERABLE, List.of(type)));

		while (value.hasNext()) {
			context.incrementScope();

			context.addVariable(new Variable(type, argument.getName(), value.next()));
			callAccept(statement.getBody());

			context.decrementScope();
		}
	}

	@Override
	public void visit(AssignmentStatement statement) {
		var context = contexts.getLast();
		var previousValue = context.findVariable(statement.getName())
				.or(() -> GLOBAL_CONTEXT.findVariable(statement.getName()))
				.orElseThrow(NoSuchVariableException::new);

		callAccept(statement.getValue());
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
		callAccept(statement.getExpression());
		throw new ReturnCalled();
	}

	@Override
	public void visit(BlockStatement block) {
		var context = contexts.getLast();
		context.incrementScope();

		for (var statement : block.getStatements()) {
			callAccept(statement);
		}

		context.decrementScope();
	}

	@Override
	public void visit(BinaryLogicalExpression expression) {
		callAccept(expression.getLeft());
		var left = retrieveResult(BOOLEAN_TYPE);

		if (expression.needsFurtherProcessing(left.isBool())) {
			callAccept(expression.getRight());
			var right = retrieveResult(BOOLEAN_TYPE);
			result = Result.ok(new BooleanValue(expression.evaluate(left.isBool(), right.isBool())));
		} else {
			result = Result.ok(new BooleanValue(left.isBool()));
		}
	}

	@Override
	public void visit(NegateLogicalExpression expression) {
		callAccept(expression.getExpression());
		var value = retrieveResult(BOOLEAN_TYPE);
		result = Result.ok(new BooleanValue(!value.isBool()));
	}

	@Override
	public void visit(BooleanExpression value) {
		result = Result.ok(new BooleanValue(value.getValue()));
	}

	@Override
	public void visit(RelationLogicalExpression expression) {
		callAccept(expression.getLeft());
		var left = retrieveResult();

		boolean value;
		if (Objects.equals(left.getType(), INTEGER_TYPE)) {
			callAccept(expression.getRight());
			var right = retrieveResult(left.getType());
			value = expression.evaluate(left.getInteger(), right.getInteger());
		} else if (Objects.equals(left.getType(), FLOATING_POINT_TYPE)) {
			callAccept(expression.getRight());
			var right = retrieveResult(left.getType());
			value = expression.evaluate(left.getFloatingPoint(), right.getFloatingPoint());
		} else {
			throw new CouldNotCompareNonNumericValues(expression.getPosition());
		}
		result = Result.ok(new BooleanValue(value));
	}

	@Override
	public void visit(EqualityRelationLogicalExpression expression) {
		callAccept(expression.getLeft());
		var left = retrieveResult();

		boolean value;
		if (Objects.equals(left.getType(), INTEGER_TYPE)) {
			callAccept(expression.getRight());
			var right = retrieveResult(left.getType());
			value = expression.evaluate(left.getInteger(), right.getInteger());
		} else if (Objects.equals(left.getType(), FLOATING_POINT_TYPE)) {
			callAccept(expression.getRight());
			var right = retrieveResult(left.getType());
			value = expression.evaluate(left.getFloatingPoint(), right.getFloatingPoint());
		} else if (Objects.equals(left.getType(), STRING_TYPE)) {
			callAccept(expression.getRight());
			var right = retrieveResult(left.getType());
			value = expression.evaluate(left.getString(), right.getString());
		} else {
			throw new CouldNotCompareNonNumericValues(expression.getPosition());
		}
		result = Result.ok(new BooleanValue(value));
	}

	@Override
	public void visit(BinaryArithmeticExpression expression) {
		callAccept(expression.getLeft());
		var left = retrieveResult();

		if (Objects.equals(left.getType(), INTEGER_TYPE)) {
			callAccept(expression.getRight());
			var right = retrieveResult(left.getType());
			var value = expression.evaluate(left.getInteger(), right.getInteger());
			result = Result.ok(new IntegerValue(value));
		} else if (Objects.equals(left.getType(), FLOATING_POINT_TYPE)) {
			callAccept(expression.getRight());
			var right = retrieveResult(left.getType());
			var value = expression.evaluate(left.getFloatingPoint(), right.getFloatingPoint());
			result = Result.ok(new FloatingPointValue(value));
		} else {
			throw new CouldNotPerformArithmeticOperationOnNonNumericType();
		}
	}

	@Override
	public void visit(NegationArithmeticExpression expression) {
		callAccept(expression.getExpression());
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
		callAccept(expression.getObject());
		var object = retrieveResult();

		if (object instanceof TupleValue tuple) {
			var value = tuple.get(expression.getIdentifier()).orElseThrow(NoSuchTupleElement::new);
			result = Result.ok(value);
		} else {
			throw new TypesDoNotMatchException(object.getType(), new TypeDeclaration(ValueType.TUPLE));
		}
	}

	@Override
	public void visit(MethodCallExpression expression) {
		callAccept(expression.getObject());
		var object = retrieveResult();

		if (object instanceof MapValue map) {
			var call = expression.getFunction();
			var arguments = new ArrayList<Value>();

			for (var argument : call.getArguments()) {
				callAccept(argument);
				arguments.add(retrieveResult());
			}

			var value = map.findMethod(call.getFunction())
					.orElseThrow(() -> new NoSuchFunctionException(call.getFunction()))
					.apply(arguments);

			result = value.map(Result::ok).orElseGet(Result::empty);
		} else {
			throw new ObjectDoesNotSupportMethodCallsException();
		}
	}

	@Override
	public void visit(IdentifierExpression expression) {
		var context = contexts.getLast();
		var variable = context.findVariable(expression.getName())
				.or(() -> GLOBAL_CONTEXT.findVariable(expression.getName()))
				.or(() -> Optional.ofNullable(functionDefinitions.get(expression.getName()))
						.map(it -> new ComparatorValue(it, convertFunctionToComparator(it)))
						.map(it -> new Variable(it.getType(), expression.getName(), it))
				)
				.orElseThrow(NoSuchVariableException::new);
		result = Result.ok(variable.getValue());
	}

	private Comparator<Value> convertFunctionToComparator(FunctionDefinitionStatement statement) {
		return (o1, o2) -> {
			var context = contexts.getLast();
			context.incrementScope();
			context.addVariable(new Variable(o1.getType(), "~~o1~~", o1));
			context.addVariable(new Variable(o1.getType(), "~~o2~~", o2));
			var call = new FunctionCallExpression(
					statement.getName(),
					List.of(new IdentifierExpression("~~o1~~", null), new IdentifierExpression("~~o2~~", null)),
					null
			);
			callAccept(call);
			context.decrementScope();
			return retrieveResult(INTEGER_TYPE).getInteger();
		};
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

		var context = new Context(declaration.getName(), currentPosition);

		for (int i = 0; i < arguments.size(); i++) {
			callAccept(arguments.get(i));
			var argument = declaration.getArguments().get(i);
			var variable = retrieveResult(argument.getType());
			context.addVariable(new Variable(argument.getType(), argument.getName(), variable));
		}

		contexts.add(context);
		if (contexts.size() > MAX_STACK_SIZE) {
			throw new MaxFunctionStackSizeReachedException();
		}

		try {
			callAccept(declaration.getBody());
			if (Objects.equals(declaration.getReturnType(), VOID_TYPE)) {
				result = Result.empty();
			} else {
				throw new ReturnValueExpectedException();
			}
		} catch (ReturnCalled exception) {
			validateType(result.getValue().getType(), declaration.getReturnType());
		}

		contexts.removeLast();
	}

	@Override
	@SuppressWarnings("java:S3864")
	public void visit(SelectExpression expression) {
		var context = contexts.getLast();
		callAccept(expression.getFrom().getValue());
		var database = retrieveIterable();

		var alias = expression.getFrom().getKey();

		List<List<Variable>> entriesVariables = new ArrayList<>();
		while (database.hasNext()) {
			context.incrementScope();
			var entry = database.next();
			var currentVariable = new Variable(entry.getType(), alias, entry);
			context.addVariable(currentVariable);

			handleJoins(expression, new LinkedList<>(expression.getJoin()))
					.stream()
					.peek(it -> it.add(currentVariable))
					.forEach(entriesVariables::add);

			context.decrementScope();
		}

		entriesVariables = handleGroupBy(expression, entriesVariables);
		entriesVariables = handleHaving(expression, entriesVariables);

		var entries = new ArrayList<Pair<Value, List<Value>>>();
		for (var variables : entriesVariables) {
			context.incrementScope();
			variables.forEach(context::addVariable);

			var entry = handleSelectWithOrderBy(expression);
			entries.add(entry);

			context.decrementScope();
		}

		var finalResult = entries.stream()
				.sorted((first, second) -> {
					var firstList = first.getValue();
					var secondList = second.getValue();

					for (var i = 0; i < firstList.size(); i++) {
						var comparison = firstList.get(i).compareTo(secondList.get(i));
						if (comparison != 0) {
							return comparison;
						}
					}
					return 0;
				})
				.map(Pair::getKey)
				.toList();

		var type = finalResult.stream()
				.findAny()
				.map(Value::getType)
				.map(TypeDeclaration::getTypes)
				.map(types -> List.of(new TypeDeclaration(ValueType.TUPLE, types)))
				.orElseGet(List::of);

		result = Result.ok(
				new IterableValue(
						new TypeDeclaration(ValueType.ITERABLE, type),
						finalResult
				)
		);
	}

	@SuppressWarnings("java:S3864")
	private List<List<Variable>> handleJoins(
			SelectExpression select,
			LinkedList<Tuple3<String, Expression, Expression>> joinExpressions
	) {
		var results = new ArrayList<List<Variable>>();
		if (joinExpressions.isEmpty()) {
			if (handleWhere(select)) {
				results.add(new ArrayList<>());
			}
			return results;
		}

		var join = joinExpressions.pollFirst();
		var context = contexts.getLast();
		var alias = join._1;
		callAccept(join._2);
		var entries = retrieveIterable();

		while (entries.hasNext()) {
			context.incrementScope();

			var entry = entries.next();
			var currentVariable = new Variable(entry.getType(), alias, entry);
			context.addVariable(currentVariable);

			callAccept(join._3);
			var on = retrieveResult(BOOLEAN_TYPE);
			if (on.isBool()) {
				handleJoins(select, joinExpressions)
						.stream()
						.peek(it -> it.add(currentVariable))
						.forEach(results::add);
			}

			context.decrementScope();
		}

		return results;
	}

	private boolean handleWhere(SelectExpression select) {
		callAccept(select.getWhere());
		return retrieveResult(BOOLEAN_TYPE).isBool();
	}

	private List<List<Variable>> handleGroupBy(SelectExpression select, List<List<Variable>> entriesVariables) {
		var groupBy = select.getGroupBy();
		if (groupBy.isEmpty()) {
			return entriesVariables;
		}
		var context = contexts.getLast();
		var entries = new ArrayList<List<Variable>>();
		var distinct = new HashSet<List<Value>>();
		for (var variables : entriesVariables) {
			context.incrementScope();
			variables.forEach(context::addVariable);

			var groupByValues = new ArrayList<Value>();
			for (var expression : groupBy) {
				callAccept(expression);
				groupByValues.add(retrieveResult());
			}

			if (!distinct.contains(groupByValues)) {
				distinct.add(groupByValues);
				entries.add(variables);
			}

			context.decrementScope();
		}
		return entries;
	}

	private List<List<Variable>> handleHaving(SelectExpression select, List<List<Variable>> entriesVariables) {
		return entriesVariables.stream()
				.filter(it -> {
					var context = contexts.getLast();
					context.incrementScope();
					it.forEach(context::addVariable);
					callAccept(select.getHaving());
					var condition = retrieveResult(BOOLEAN_TYPE);
					context.decrementScope();
					return condition.isBool();
				})
				.toList();
	}

	private Pair<Value, List<Value>> handleSelectWithOrderBy(SelectExpression select) {
		callAccept(select.getSelect());
		return Pair.of(
				retrieveResult(ValueType.TUPLE),
				handleOrderBy(select)
		);
	}

	private List<Value> handleOrderBy(SelectExpression select) {
		var orderBy = new ArrayList<Value>();
		for (var pair : select.getOrderBy()) {
			var ascending = pair.getValue();
			var expression = Boolean.TRUE.equals(ascending)
					? pair.getKey()
					: new NegationArithmeticExpression(pair.getKey(), select.getPosition());
			callAccept(expression);
			orderBy.add(retrieveResult());
		}
		return orderBy;
	}

	@Override
	public void visit(TupleExpression expression) {
		var elements = new HashMap<String, Value>();
		var types = new ArrayList<TypeDeclaration>();
		for (var entry : expression.getElements().entrySet()) {
			callAccept(entry.getValue());
			var value = retrieveResult();
			var type = value.getType();
			types.add(type);
			elements.put(entry.getKey(), value);
		}
		result = Result.ok(
				new TupleValue(new TypeDeclaration(ValueType.TUPLE, types), elements)
		);
	}

	@Override
	public void visit(TupleElement expression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(MapExpression expression) {
		var entries = new HashMap<Value, Value>();

		if (expression.getElements().isEmpty()) {
			result = Result.ok(new MapValue(new TypeDeclaration(ValueType.MAP), entries));
			return;
		}

		var entrySet = new LinkedList<>(expression.getElements().entrySet());
		var first = entrySet.removeFirst();

		callAccept(first.getKey());
		var key = retrieveResult();
		var keyType = key.getType();

		callAccept(first.getValue());
		var value = retrieveResult();
		var valueType = value.getType();

		entries.put(key, value);

		for (var entry : expression.getElements().entrySet()) {
			callAccept(entry.getKey());
			key = retrieveResult(keyType);

			callAccept(entry.getValue());
			value = retrieveResult(valueType);

			entries.put(key, value);
		}

		result = Result.ok(new MapValue(new TypeDeclaration(ValueType.MAP, List.of(keyType, valueType)), entries));
	}

	private static final Map<TypeDeclaration, Map<TypeDeclaration, Function<Value, Value>>> explicitCastConverters = Map.of(
			INTEGER_TYPE, Map.of(
					INTEGER_TYPE, Function.identity(),
					FLOATING_POINT_TYPE, it -> new IntegerValue((int) it.getFloatingPoint()),
					BOOLEAN_TYPE, it -> new IntegerValue(it.isBool() ? 1 : 0)
			),
			FLOATING_POINT_TYPE, Map.of(
					INTEGER_TYPE, it -> new FloatingPointValue(it.getInteger()),
					FLOATING_POINT_TYPE, Function.identity(),
					BOOLEAN_TYPE, it -> new FloatingPointValue(it.isBool() ? 1.0D : 0.0D)
			),
			BOOLEAN_TYPE, Map.of(
					INTEGER_TYPE, it -> new BooleanValue(it.getInteger() != 0),
					FLOATING_POINT_TYPE, it -> new BooleanValue(it.getFloatingPoint() != 0),
					BOOLEAN_TYPE, Function.identity()
			),
			STRING_TYPE, Map.of(
					INTEGER_TYPE, it -> new StringValue(String.valueOf(it.getInteger())),
					FLOATING_POINT_TYPE, it -> new StringValue(String.valueOf(it.getFloatingPoint())),
					BOOLEAN_TYPE, it -> new StringValue(String.valueOf(it.isBool())),
					STRING_TYPE, Function.identity()
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

		callAccept(expression.getExpression());
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

		validateType(message.getType(), STRING_TYPE);

		var value = message.getValue();
		out.println(value.getString());
	}

	private Value retrieveResult(TypeDeclaration type) {
		var value = retrieveResult();

		var currentType = value.getType();
		var valueType = currentType.getValueType();
		if (valueType.isComplex()
				&& currentType.getTypes().isEmpty()) {
			if (valueType == ValueType.MAP && value instanceof MapValue mapValue) {
				value = mapValue.toBuilder().type(type).build();
			} else if (valueType == ValueType.ITERABLE && value instanceof IterableValue iterableValue) {
				iterableValue.setType(type);
			}
		}

		validateType(value.getType(), type);

		return value;
	}

	private Value retrieveResult(ValueType type) {
		var value = retrieveResult();

		if (value.getType().getValueType() != type) {
			throw new TypesDoNotMatchException(value.getType(), new TypeDeclaration(type));
		}

		return value;
	}

	private Value retrieveResult() {
		if (!result.isPresent()) {
			throw new ExpressionDidNotEvaluateException();
		}

		return result.getValue();
	}

	private Value retrieveIterable() {
		var retrieved = retrieveResult();
		if (retrieved instanceof IterableValue iterableValue) {
			return iterableValue;
		} else if (retrieved instanceof MapValue mapValue) {
			return mapValue.iterable();
		} else {
			throw new NoSuchFunctionException("iterable");
		}
	}

	private void validateType(TypeDeclaration provided, TypeDeclaration expected) {
		if (!Objects.equals(provided, expected)) {
			throw new TypesDoNotMatchException(provided, expected);
		}
	}

	private <T extends Node> void callAccept(T expression) {
		currentPosition = expression.getPosition();
		expression.accept(this);
	}
}
