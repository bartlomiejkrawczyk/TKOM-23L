package org.example.ast

import org.example.ast.expression.IdentifierExpression
import org.example.ast.type.IntegerValue
import org.example.ast.type.TupleElement
import org.example.interpreter.Environment
import org.example.token.Position
import spock.lang.Specification

class ExpressionTest extends Specification {

	def 'Should evaluate correctly'() {
		given:
		var expression = new IntegerValue(1, new Position())
		var environment = new Environment()

		when:
		expression.evaluate(environment)

		then:
		noExceptionThrown()
	}

	def 'Should get program position as first character in file'() {
		given:
		var program = new Program(Map.of(), List.of())

		expect:
		program.getPosition() == new Position(1, 1)
	}

	def 'Should get correct position of tuple element'() {
		given:
		var position = new Position()
		var element = new TupleElement("name", new IdentifierExpression("name", position))

		expect:
		element.getPosition() == position
	}
}
