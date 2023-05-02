package org.example.ast

import org.example.ast.expression.BlockExpression
import org.example.ast.expression.arithmetic.AddArithmeticExpression
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.ast.type.IntegerValue
import org.example.ast.type.TypeDeclaration
import org.example.interpreter.Environment
import spock.lang.Specification

class ExpressionTest extends Specification {

	def 'Should print tree correctly'() {
		given:
		var expression = new FunctionDefinitionStatement(
				"main",
				List.of(),
				new TypeDeclaration(ValueType.VOID),
				new BlockExpression(
						List.of(
								new AddArithmeticExpression(
										new IntegerValue(1),
										new IntegerValue(2)
								)
						)
				)
		)
		expect:
		expression.print() == """FunctionDefinitionStatement(name=main, arguments=[], returnType=TypeDeclaration(valueType=VOID, types=[]))
`--- BlockExpression()
     `--- AddArithmeticExpression()
          |--- IntegerValue(value=1)
          `--- IntegerValue(value=2)
"""
	}

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
