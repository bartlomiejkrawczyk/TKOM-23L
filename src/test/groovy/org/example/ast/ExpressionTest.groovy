package org.example.ast

import org.example.ast.expression.ArithmeticExpression
import org.example.ast.expression.BlockExpression
import org.example.ast.expression.FunctionDefinitionExpression
import org.example.ast.type.FloatingPointValue
import org.example.ast.type.IntegerValue
import org.example.token.TokenType
import spock.lang.Specification

class ExpressionTest extends Specification {

	def 'Should print tree correctly'() {
		given:
		var expression = new FunctionDefinitionExpression(
				"main",
				List.of(),
				new BlockExpression(
						List.of(
								new ArithmeticExpression(
										TokenType.PLUS,
										new IntegerValue(10),
										new FloatingPointValue(1.25)
								)
						)
				)
		)
		expect:
		expression.print() == """FunctionDefinitionExpression(name=main, arguments={})
`--- BlockExpression()
     `--- ArithmeticExpression(operation=PLUS)
          |--- IntegerValue(value=10)
          `--- FloatingPointValue(value=1.25)
"""
	}
}
