package org.example.visitor;

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

	void visit(BooleanValue value);

	void visit(RelationLogicalExpression expression);

	void visit(BinaryArithmeticExpression expression);

	void visit(NegationArithmeticExpression expression);

	void visit(IntegerValue value);

	void visit(FloatingPointValue value);

	void visit(StringValue value);

	void visit(TupleCallExpression expression);

	void visit(MethodCallExpression expression);

	void visit(IdentifierExpression expression);

	void visit(FunctionCallExpression expression);

	void visit(SelectExpression expression);

	void visit(TupleExpression expression);

	void visit(TupleElement expression);

	void visit(MapExpression expression);

	void visit(ExplicitCastExpression expression);
}
