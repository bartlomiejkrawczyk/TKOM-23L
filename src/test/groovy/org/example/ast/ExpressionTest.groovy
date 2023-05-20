package org.example.ast

import org.example.ast.expression.BlockStatement
import org.example.ast.expression.IdentifierExpression
import org.example.ast.expression.arithmetic.AddArithmeticExpression
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.ast.type.IntegerExpression
import org.example.ast.type.TupleElement
import org.example.ast.type.TypeDeclaration
import org.example.interpreter.Environment
import org.example.token.Position
import spock.lang.Specification

class ExpressionTest extends Specification {

	def 'Should evaluate correctly'() {
		given:
		var expression = new IntegerExpression(1, new Position())
		var environment = new Environment()

		when:
		expression.evaluate(environment)

		then:
		noExceptionThrown()
	}

	def 'Should get program position as first character in file'() {
		given:
		var program = new Program(Map.of(), Map.of())

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

	def 'Should print correctly'() {
		given:
		var position = new Position(1, 1)
		var statement = new FunctionDefinitionStatement(
				"main",
				List.of(),
				new TypeDeclaration(ValueType.VOID),
				new BlockStatement(
						List.of(
								new AddArithmeticExpression(
										new IntegerExpression(1, position),
										new IntegerExpression(2, position),
										position
								)
						),
						position
				),
				position
		)
		expect:
		statement.print() == """FunctionDefinitionStatement(name=main, arguments=[], returnType=TypeDeclaration(valueType=VOID, types=[]), position=Position(line=1, characterNumber=1))
`--- BlockStatement(position=Position(line=1, characterNumber=1))
     `--- AddArithmeticExpression(position=Position(line=1, characterNumber=1))
          |--- IntegerExpression(value=1, position=Position(line=1, characterNumber=1))
          `--- IntegerExpression(value=2, position=Position(line=1, characterNumber=1))
"""
	}
}
