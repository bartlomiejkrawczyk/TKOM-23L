package org.example.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ast.Program;
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
import org.example.ast.type.BooleanValue;
import org.example.ast.type.FloatingPointValue;
import org.example.ast.type.IntegerValue;
import org.example.ast.type.StringValue;
import org.example.ast.type.TupleElement;
import org.example.error.ErrorHandler;
import org.example.interpreter.model.Context;
import org.example.interpreter.model.Result;
import org.example.interpreter.model.Variable;
import reactor.util.annotation.Nullable;

@Slf4j
@RequiredArgsConstructor
public class InterpretingVisitor implements Visitor, Interpreter {

	private static final String MAIN_FUNCTION_NAME = "main";
	private static final String NOT_IMPLEMENTED_YET = "Not implemented yet!";
	private final ErrorHandler errorHandler;
	// TODO: function definitions
	// TODO: global variables
	private final List<Context> contexts = new ArrayList<>();
	private final Map<String, FunctionDefinitionStatement> functionDefinitions = new HashMap<>();
	private final Map<String, Variable> globalVariables = new HashMap<>();

	@Nullable
	private Result result = null;

	@Override
	public void execute(Program program) {
		program.accept(this);
	}

	@Override
	public void visit(Program program) {
		functionDefinitions.putAll(program.getFunctionDefinitions());

		for (var declaration : program.getDeclarations().values()) {
			declaration.accept(this);

			var argument = declaration.getArgument();
			// TODO: refactor xD
			globalVariables.put(argument.getName(), new Variable(argument.getName(), null, null, null, null));
		}
		var main = new FunctionCallExpression(MAIN_FUNCTION_NAME, List.of(), program.getPosition());
		main.accept(this);
	}

	@Override
	public void visit(FunctionDefinitionStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(DeclarationStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(IfStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(WhileStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(ForStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(AssignmentStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(ReturnStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(BlockStatement statement) {
		log.info("block");
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
	public void visit(BooleanValue value) {
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
	public void visit(IntegerValue value) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(FloatingPointValue value) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(StringValue value) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
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
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
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
	}

	@Override
	public void visit(MapExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public void visit(ExplicitCastExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}
}
