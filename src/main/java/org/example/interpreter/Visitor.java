package org.example.interpreter;

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
import org.example.interpreter.model.custom.PrintFunction;

public interface Visitor {

	void visit(Program program);

	void visit(FunctionDefinitionStatement statement);

	void visit(DeclarationStatement statement);

	void visit(IfStatement statement);

	void visit(WhileStatement statement);

	void visit(ForStatement statement);

	void visit(AssignmentStatement statement);

	void visit(ReturnStatement statement);

	void visit(BlockStatement statement);

	void visit(BinaryLogicalExpression expression);

	void visit(NegateLogicalExpression expression);

	void visit(BooleanExpression value);

	void visit(RelationLogicalExpression expression);

	void visit(EqualityRelationLogicalExpression expression);

	void visit(BinaryArithmeticExpression expression);

	void visit(NegationArithmeticExpression expression);

	void visit(IntegerExpression value);

	void visit(FloatingPointExpression value);

	void visit(StringExpression value);

	void visit(TupleCallExpression expression);

	void visit(MethodCallExpression expression);

	void visit(IdentifierExpression expression);

	void visit(FunctionCallExpression expression);

	void visit(SelectExpression expression);

	void visit(TupleExpression expression);

	void visit(TupleElement expression);

	void visit(MapExpression expression);

	void visit(ExplicitCastExpression expression);

	void visit(PrintFunction expression);
}
