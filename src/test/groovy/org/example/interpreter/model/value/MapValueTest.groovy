package org.example.interpreter.model.value

import org.example.ast.ValueType
import org.example.ast.type.TypeDeclaration
import org.example.interpreter.InterpreterUtility
import org.example.interpreter.error.ArgumentListDoesNotMatch
import org.example.interpreter.error.TypesDoNotMatchException
import spock.lang.Specification

class MapValueTest extends Specification {

	def 'Should throw an exception when invalid number of arguments are provided to method'() {
		given:
		var map = new MapValue(
				new TypeDeclaration(ValueType.MAP, List.of(InterpreterUtility.STRING_TYPE, InterpreterUtility.INTEGER_TYPE)),
				Map.of()
		)

		when:
		map.findMethod("contains").ifPresent(it -> it.apply(List.of()))

		then:
		thrown(ArgumentListDoesNotMatch)
	}

	def 'Should throw an exception when invalid argument types are provided to method'() {
		given:
		var map = new MapValue(
				new TypeDeclaration(ValueType.MAP, List.of(InterpreterUtility.STRING_TYPE, InterpreterUtility.INTEGER_TYPE)),
				Map.of()
		)

		when:
		map.findMethod("contains").ifPresent(it -> it.apply(List.of(new IntegerValue(1))))

		then:
		thrown(TypesDoNotMatchException)
	}
}