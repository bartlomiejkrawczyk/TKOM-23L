package org.example.ast


import org.example.ast.type.IntegerValue
import org.example.interpreter.Environment
import spock.lang.Specification

class ExpressionTest extends Specification {

	def 'Should evaluate correctly'() {
		given:
		var expression = new IntegerValue(1)
		var environment = new Environment()

		when:
		expression.evaluate(environment)

		then:
		noExceptionThrown()
	}
}
