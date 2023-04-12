package org.example.ast


import org.example.ast.expression.ArithmeticExpression
import org.example.ast.expression.FloatingPointExpression
import org.example.ast.expression.FunctionDefinitionExpression
import org.example.ast.expression.IntegerExpression
import org.example.token.TokenType
import spock.lang.Specification

class ExpressionTest extends Specification {

	def 'Should print tree correctly'() {
		given:
		var expression = new FunctionDefinitionExpression(
				"main",
				Map.of(),
				new ArithmeticExpression(
						TokenType.PLUS,
						new IntegerExpression(10),
						new FloatingPointExpression(1.25)
				)
		)
		expect:
		expression.print() == """FunctionDefinitionExpression(name=main, arguments={})
`--- ArithmeticExpression(operation=PLUS)
     |--- IntegerExpression(value=10)
     `--- FloatingPointExpression(value=1.25)
"""
	}
}