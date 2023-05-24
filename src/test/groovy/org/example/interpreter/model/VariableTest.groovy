package org.example.interpreter.model

import org.example.interpreter.InterpreterUtility
import org.example.interpreter.error.TypesDoNotMatchException
import org.example.interpreter.model.value.StringValue
import spock.lang.Specification


class VariableTest extends Specification {

	def 'Should throw an exception when different types are provided'() {
		when:
		new Variable(InterpreterUtility.INTEGER_TYPE, "test", new StringValue("test"))

		then:
		thrown(TypesDoNotMatchException)
	}
}
