package org.example.interpreter;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ast.Node;
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
import org.example.ast.statement.IfStatement;
import org.example.ast.statement.WhileStatement;
import org.example.ast.type.BooleanValue;
import org.example.ast.type.IntegerValue;
import org.example.ast.type.StringValue;
import org.example.error.ErrorHandler;
import org.example.interpreter.model.Context;
import org.example.visitor.Visitor;

@Slf4j
@RequiredArgsConstructor
public class InterpretingVisitor implements Visitor, Interpreter {

	public static final String NOT_IMPLEMENTED_YET = "Not implemented yet!";
	private final Program program;
	private final ErrorHandler errorHandler;
	// TODO: function definitions
	// TODO: global variables
	private final List<Context> contexts = new ArrayList<>();

	@Override
	public void execute() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(Program program) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(IfStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(WhileStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(ForStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(DeclarationStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(AssignmentStatement statement) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(BlockStatement statement) {
		log.info("block");
	}

	public void accept(BinaryLogicalExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(NegateLogicalExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(BooleanValue value) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(RelationLogicalExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(BinaryArithmeticExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(NegationArithmeticExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(IntegerValue value) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(StringValue value) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(TupleCallExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(MethodCallExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(IdentifierExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(FunctionCallExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(SelectExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(TupleExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(MapExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	public void accept(ExplicitCastExpression expression) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}

	@Override
	public <T extends Node> void accept(T node) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
	}
}
