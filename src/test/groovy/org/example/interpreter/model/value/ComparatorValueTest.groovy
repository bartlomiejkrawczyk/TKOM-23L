package org.example.interpreter.model.value

import org.example.ast.expression.Argument
import org.example.ast.expression.BlockStatement
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.interpreter.InterpreterUtility
import org.example.interpreter.error.ArgumentListDoesNotMatch
import org.example.interpreter.error.TypesDoNotMatchException
import org.example.interpreter.model.Value
import spock.lang.Specification


class ComparatorValueTest extends Specification {

	def 'Should throw an exception when function arguments do not match'() {
		when:
		new ComparatorValue(
				new FunctionDefinitionStatement(
						"compare",
						List.of(
								new Argument("first", InterpreterUtility.INTEGER_TYPE),
								new Argument("second", InterpreterUtility.BOOLEAN_TYPE)
						),
						InterpreterUtility.INTEGER_TYPE,
						new BlockStatement(List.of(), null),
						null
				),
				Comparator.comparing(Value::getInteger)
		)

		then:
		thrown(TypesDoNotMatchException)
	}

	def 'Should throw an exception when function return type is not integer'() {
		when:
		new ComparatorValue(
				new FunctionDefinitionStatement(
						"compare",
						List.of(
								new Argument("first", InterpreterUtility.INTEGER_TYPE),
								new Argument("second", InterpreterUtility.INTEGER_TYPE)
						),
						InterpreterUtility.BOOLEAN_TYPE,
						new BlockStatement(List.of(), null),
						null
				),
				Comparator.comparing(Value::getInteger)
		)

		then:
		thrown(TypesDoNotMatchException)
	}

	def 'Should throw an exception when function has different number of arguments than 2'() {
		when:
		new ComparatorValue(
				new FunctionDefinitionStatement(
						"compare",
						List.of(
								new Argument("first", InterpreterUtility.INTEGER_TYPE)
						),
						InterpreterUtility.BOOLEAN_TYPE,
						new BlockStatement(List.of(), null),
						null
				),
				Comparator.comparing(Value::getInteger)
		)

		then:
		thrown(ArgumentListDoesNotMatch)
	}

}