package org.example.visitor

import org.example.ast.ValueType
import org.example.ast.expression.BlockExpression
import org.example.ast.expression.arithmetic.AddArithmeticExpression
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.ast.type.IntegerValue
import org.example.ast.type.TypeDeclaration
import spock.lang.Specification


class VisitorTest extends Specification {

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
		var visitor = new PrintingVisitor()

		when:
		visitor.accept(expression)

		then:
		visitor.print() == """FunctionDefinitionStatement(name=main, arguments=[], returnType=TypeDeclaration(valueType=VOID, types=[]))
`--- BlockExpression()
     `--- AddArithmeticExpression()
          |--- IntegerValue(value=1)
          `--- IntegerValue(value=2)
"""
	}
}
