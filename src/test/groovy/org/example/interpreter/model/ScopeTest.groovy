package org.example.interpreter.model

import org.example.interpreter.InterpreterUtility
import org.example.interpreter.error.TypesDoNotMatchException
import org.example.interpreter.model.value.IntegerValue
import org.example.interpreter.model.value.StringValue
import spock.lang.Specification


class ScopeTest extends Specification {

	def 'Should throw an exception when trying to update variable with different type'() {
		given:
		var scope = new Scope()

		scope.addVariable(new Variable(InterpreterUtility.STRING_TYPE, "variable", new StringValue("hello")))

		when:
		scope.updateVariable("variable", new IntegerValue(1))

		then:
		thrown(TypesDoNotMatchException)
	}
}
