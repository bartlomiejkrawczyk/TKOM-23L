package org.example.visitor

import org.example.ast.ValueType
import org.example.ast.expression.BlockExpression
import org.example.ast.expression.arithmetic.AddArithmeticExpression
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.ast.type.IntegerValue
import org.example.ast.type.TypeDeclaration
import org.example.token.Position
import spock.lang.Specification


class VisitorTest extends Specification {

	var position = Position.builder().characterNumber(1).line(1).build()

	def 'Should print tree correctly'() {
		given:
		var expression = new FunctionDefinitionStatement(
				"main",
				List.of(),
				new TypeDeclaration(ValueType.VOID),
				new BlockExpression(
						List.of(
								new AddArithmeticExpression(
										new IntegerValue(1, position),
										new IntegerValue(2, position),
										position
								)
						),
						position
				),
				position
		)
		var visitor = new PrintingVisitor()

		when:
		visitor.accept(expression)

		then:
		visitor.print() == """FunctionDefinitionStatement(name=main, arguments=[], returnType=TypeDeclaration(valueType=VOID, types=[]), position=Position(line=1, characterNumber=1))
`--- BlockExpression(position=Position(line=1, characterNumber=1))
     `--- AddArithmeticExpression(position=Position(line=1, characterNumber=1))
          |--- IntegerValue(value=1, position=Position(line=1, characterNumber=1))
          `--- IntegerValue(value=2, position=Position(line=1, characterNumber=1))
"""
	}
}
