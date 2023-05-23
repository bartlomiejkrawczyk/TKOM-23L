package org.example.interpreter

import org.example.ast.ValueType
import org.example.ast.expression.BlockStatement
import org.example.ast.expression.IdentifierExpression
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.ast.type.TupleElement
import org.example.ast.type.TypeDeclaration
import org.example.error.ErrorHandler
import org.example.interpreter.error.CriticalInterpreterException
import org.example.token.Position
import spock.lang.Specification


class InterpretingVisitorTest extends Specification {

	var errorHandler = Mock(ErrorHandler)

	def 'Should be able to determine type'() {
		given:
		var visitor = new InterpretingVisitor(errorHandler, System.out)

		expect:
		new BlockStatement(List.of(), new Position()).accept(visitor)
	}

	def 'Should throw an exception when unreachable statement is called'() {
		given:
		var visitor = new InterpretingVisitor(errorHandler, System.out)

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
}
