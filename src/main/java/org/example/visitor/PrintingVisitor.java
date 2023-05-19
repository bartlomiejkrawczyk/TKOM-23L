package org.example.visitor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
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
import org.example.ast.statement.FunctionDefinitionStatement;
import org.example.ast.statement.IfStatement;
import org.example.ast.statement.ReturnStatement;
import org.example.ast.statement.WhileStatement;
import org.example.ast.type.BooleanValue;
import org.example.ast.type.FloatingPointValue;
import org.example.ast.type.IntegerValue;
import org.example.ast.type.StringValue;
import org.example.ast.type.TupleElement;
import org.example.lexer.CharactersUtility;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrintingVisitor implements Visitor {

	// THIS PRINTING VISITOR IS USELESS - it should be one Node function, as it was previously xD
	final StringBuilder buffer = new StringBuilder();
	String prefix = StringUtils.EMPTY;
	String childrenPrefix = StringUtils.EMPTY;

	public String print() {
		return buffer.toString();
	}

	private void visitNode(Node node) {
		var previousPrefix = prefix;
		var previousChildrenPrefix = childrenPrefix;

		buffer.append(prefix);
		buffer.append(node.toString());
		buffer.append(CharactersUtility.NEW_LINE);

		var children = node.getChildrenExpressions().iterator();
		while (children.hasNext()) {
			var expression = children.next();
			if (children.hasNext()) {
				prefix = previousChildrenPrefix + "|--- ";
				childrenPrefix = previousChildrenPrefix + "|    ";
				expression.accept(this);
			} else {
				prefix = previousChildrenPrefix + "`--- ";
				childrenPrefix = previousChildrenPrefix + "     ";
				expression.accept(this);
			}
		}

		prefix = previousPrefix;
		childrenPrefix = previousChildrenPrefix;
	}

	@Override
	public void visit(Program program) {
		visitNode(program);
	}

	@Override
	public void visit(FunctionDefinitionStatement statement) {
		visitNode(statement);
	}

	@Override
	public void visit(DeclarationStatement statement) {
		visitNode(statement);
	}

	@Override
	public void visit(IfStatement statement) {
		visitNode(statement);
	}

	@Override
	public void visit(WhileStatement statement) {
		visitNode(statement);
	}

	@Override
	public void visit(ForStatement statement) {
		visitNode(statement);
	}

	@Override
	public void visit(AssignmentStatement statement) {
		visitNode(statement);
	}

	@Override
	public void visit(ReturnStatement statement) {
		visitNode(statement);
	}

	@Override
	public void visit(BlockStatement statement) {
		visitNode(statement);
	}

	@Override
	public void visit(BinaryLogicalExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(NegateLogicalExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(BooleanValue value) {
		visitNode(value);
	}

	@Override
	public void visit(RelationLogicalExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(BinaryArithmeticExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(NegationArithmeticExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(IntegerValue value) {
		visitNode(value);
	}

	@Override
	public void visit(FloatingPointValue value) {
		visitNode(value);
	}

	@Override
	public void visit(StringValue value) {
		visitNode(value);
	}

	@Override
	public void visit(TupleCallExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(MethodCallExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(IdentifierExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(FunctionCallExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(SelectExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(TupleExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(TupleElement expression) {
		visitNode(expression);
	}

	@Override
	public void visit(MapExpression expression) {
		visitNode(expression);
	}

	@Override
	public void visit(ExplicitCastExpression expression) {
		visitNode(expression);
	}
}
