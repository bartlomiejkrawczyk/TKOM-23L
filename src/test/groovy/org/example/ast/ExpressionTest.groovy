package org.example.ast

import org.example.ast.expression.BlockExpression
import org.example.ast.expression.arithmetic.AddArithmeticExpression
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.ast.type.IntegerValue
import org.example.ast.type.TypeDeclaration
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
		expression.print() == """FunctionDefinitionStatement(name=main, arguments=[], returnType=TypeDeclaration(valueType=VOID, types=[]), body=BlockExpression(statements=[AddArithmeticExpression(left=IntegerValue(value=1), right=IntegerValue(value=2))]))
`--- BlockExpression(statements=[AddArithmeticExpression(left=IntegerValue(value=1), right=IntegerValue(value=2))])
     `--- AddArithmeticExpression(left=IntegerValue(value=1), right=IntegerValue(value=2))
          |--- IntegerValue(value=1)
          `--- IntegerValue(value=2)
"""
	}
}
