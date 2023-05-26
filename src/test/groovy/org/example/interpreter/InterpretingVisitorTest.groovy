package org.example.interpreter

import org.example.ast.Program
import org.example.ast.ValueType
import org.example.ast.expression.BlockStatement
import org.example.ast.expression.IdentifierExpression
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.ast.type.IntegerExpression
import org.example.ast.type.TupleElement
import org.example.ast.type.TypeDeclaration
import org.example.error.ErrorHandler
import org.example.interpreter.error.CriticalInterpreterException
import org.example.interpreter.error.ExpressionDidNotEvaluateException
import org.example.interpreter.error.TypesDoNotMatchException
import org.example.token.Position
import spock.lang.Specification


class InterpretingVisitorTest extends Specification {

	var errorHandler = Mock(ErrorHandler)
	var program = new Program(Map.of(), Map.of())

	def 'Should be able to determine type'() {
		given:
		var visitor = new InterpretingVisitor(errorHandler, System.out, program)

		expect:
		new BlockStatement(List.of(), new Position()).accept(visitor)
	}

	def 'Should throw an exception when unreachable statement is called'() {
		given:
		var visitor = new InterpretingVisitor(errorHandler, System.out, program)

		when:
		statement.accept(visitor)

		then:
		thrown(CriticalInterpreterException)

		where:
		statement << [
				new FunctionDefinitionStatement("", List.of(), new TypeDeclaration(ValueType.VOID), new BlockStatement(List.of(), null), null),
				new TupleElement("element", new IdentifierExpression("abc", null))
		]
	}

	def 'Should throw an exception when trying to retrieve value not present'() {
		given:
		var visitor = new InterpretingVisitor(errorHandler, System.out, program)

		and:
		new IntegerExpression(1, null).accept(visitor)

		when:
		visitor.retrieveResult(ValueType.BOOLEAN)

		then:
		thrown(TypesDoNotMatchException)
	}

	def 'Should throw an exception when result is empty'() {
		given:
		var visitor = new InterpretingVisitor(errorHandler, System.out, program)

		when:
		visitor.retrieveResult()

		then:
		thrown(ExpressionDidNotEvaluateException)
	}
}
