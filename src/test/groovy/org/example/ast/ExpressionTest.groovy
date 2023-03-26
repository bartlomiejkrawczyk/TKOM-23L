package org.example.ast

import org.example.ast.expression.*
import org.example.token.TokenType
import spock.lang.Specification

class ExpressionTest extends Specification {

	def 'Should print tree correctly'() {
		given:
		var expression = new FunctionExpression(
				new PrototypeExpression(
						"main",
						Map.of()
				),
				new BinaryExpression(
						TokenType.PLUS,
						new IntegerExpression(10),
						new FloatingPointExpression(1.25)
				)
		)
		expect:
		expression.print() == """FunctionExpression()
|--- PrototypeExpression(name=main, arguments={})
`--- BinaryExpression(operation=PLUS)
     |--- IntegerExpression(value=10)
     `--- FloatingPointExpression(value=1.25)
"""
	}
}