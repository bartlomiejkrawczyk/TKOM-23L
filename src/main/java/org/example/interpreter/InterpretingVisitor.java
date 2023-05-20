package org.example.interpreter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.example.interpreter.error.ExpressionDidNotEvaluateException;
import org.example.interpreter.error.NoSuchFunctionException;
import org.example.interpreter.error.ReturnCalled;
import org.example.interpreter.error.TypesDoNotMatchException;
import org.example.interpreter.model.Context;
import org.example.interpreter.model.Result;
import org.example.interpreter.model.Variable;
import org.example.interpreter.model.custom.PrintFunction;
import org.example.interpreter.model.value.FloatingPointValue;
import org.example.interpreter.model.value.IntegerValue;
import org.example.interpreter.model.value.StringValue;
import org.example.token.Position;

@Slf4j
@RequiredArgsConstructor
public class InterpretingVisitor implements Visitor, Interpreter {

	private static final Context GLOBAL_CONTEXT = new Context(new TypeDeclaration(ValueType.VOID), "~~main~~");
	private static final String PRINT_ARGUMENT = "~~message~~";
	private static final String MAIN_FUNCTION_NAME = "main";
	private static final String NOT_IMPLEMENTED_YET = "Not implemented yet!";
	private final ErrorHandler errorHandler;
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

	private Result result = Result.builder().present(false).build();

	@Override
	public void execute(Program program) {
		program.accept(this);
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

		if (!result.isPresent()) {
			throw new ExpressionDidNotEvaluateException();
		}

		var variable = new Variable(argument.getType(), argument.getName(), result.getValue());
		context.addVariable(variable);
	}

	@Override
	public void visit(IfStatement statement) {
		statement.getCondition().accept(this);

		if (!result.isPresent()) {
			throw new ExpressionDidNotEvaluateException();
		}

		var value = result.getValue();

		if (value.getType().getValueType() != ValueType.BOOLEAN) {
			throw new TypesDoNotMatchException(value.getType(), new TypeDeclaration(ValueType.BOOLEAN));
		}

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

		if (!result.isPresent()) {
			throw new ExpressionDidNotEvaluateException();
		}

		var value = result.getValue();

		if (value.getType().getValueType() != ValueType.BOOLEAN) {
			throw new TypesDoNotMatchException(value.getType(), new TypeDeclaration(ValueType.BOOLEAN));
		}

		while (result.getValue().getBool()) {
			statement.getBody().accept(this);

			// Do not need to check types again - condition did not change
			condition.accept(this);
		}
	}

	@Override
	public void visit(ForStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(AssignmentStatement statement) {
		var context = contexts.getLast();

		statement.getValue().accept(this);

		if (!result.isPresent()) {
			throw new ExpressionDidNotEvaluateException();
		}

		context.updateVariable(statement.getName(), result.getValue());
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
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(NegateLogicalExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(BooleanExpression value) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(RelationLogicalExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(BinaryArithmeticExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(NegationArithmeticExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(IntegerExpression expression) {
		result = Result.builder().value(new IntegerValue(expression.getValue())).build();
	}

	@Override
	public void visit(FloatingPointExpression expression) {
		result = Result.builder().value(new FloatingPointValue(expression.getValue())).build();
	}

	@Override
	public void visit(StringExpression expression) {
		result = Result.builder().value(new StringValue(expression.getValue())).build();
	}

	@Override
	public void visit(TupleCallExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(MethodCallExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(IdentifierExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
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

		// TODO: map arguments as local variables

		var context = new Context(declaration.getReturnType(), declaration.getName());

		contexts.add(context);

		try {
			declaration.getBody().accept(this);
			result.setPresent(false);
		} catch (ReturnCalled exception) {
			// TODO: validate return value
		}

		contexts.removeLast();
	}

	@Override
	public void visit(SelectExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(TupleExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(TupleElement expression) {
		// TODO: might not use this
	}

	@Override
	public void visit(MapExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(ExplicitCastExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	@SuppressWarnings("java:S106")
	public void visit(PrintFunction expression) {
		var context = contexts.getLast();

		var message = context.findVariable(PRINT_ARGUMENT);

		if (message.isPresent()) {
			System.out.println(message);
		}
	}
}
