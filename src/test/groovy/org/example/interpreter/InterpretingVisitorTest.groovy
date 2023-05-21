package org.example.interpreter

import org.example.ast.expression.BlockStatement
import org.example.error.ErrorHandler
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
}
