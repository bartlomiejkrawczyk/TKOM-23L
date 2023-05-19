package org.example.interpreter

import org.example.ast.expression.BlockStatement
import org.example.token.Position
import spock.lang.Specification


class InterpretingVisitorTest extends Specification {

	def 'Should be able to determine type'() {
		given:
		var visitor = new InterpretingVisitor()

		expect:
		visitor.accept(new BlockStatement(List.of(), new Position()))
	}
}
