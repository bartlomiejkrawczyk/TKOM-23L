package org.example.interpreter.model

import org.example.ast.ValueType
import org.example.ast.expression.Argument
import org.example.ast.expression.BlockStatement
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.ast.type.TypeDeclaration
import org.example.interpreter.InterpreterUtility
import org.example.interpreter.error.UnexpectedTypeException
import org.example.interpreter.error.UnsupportedCastException
import org.example.interpreter.model.value.BooleanValue
import org.example.interpreter.model.value.ComparatorValue
import org.example.interpreter.model.value.FloatingPointValue
import org.example.interpreter.model.value.IntegerValue
import org.example.interpreter.model.value.IterableValue
import org.example.interpreter.model.value.MapValue
import org.example.interpreter.model.value.StringValue
import org.example.interpreter.model.value.TupleValue
import spock.lang.Shared
import spock.lang.Specification

class ValueTest extends Specification {

	@Shared
	var mockFunction = new FunctionDefinitionStatement(
			"",
			List.of(
					new Argument("first", InterpreterUtility.INTEGER_TYPE),
					new Argument("second", InterpreterUtility.INTEGER_TYPE)
			),
			InterpreterUtility.INTEGER_TYPE,
			new BlockStatement(List.of(), null),
			null
	)

	@Shared
	var mockMapType = new TypeDeclaration(ValueType.MAP, List.of(InterpreterUtility.STRING_TYPE, InterpreterUtility.STRING_TYPE))

	@Shared
	var mockTupleType = new TypeDeclaration(ValueType.TUPLE, List.of(InterpreterUtility.STRING_TYPE))

	@Shared
	var mockIterableType = new TypeDeclaration(ValueType.ITERABLE, List.of(mockTupleType))

	def 'Should throw an exception when trying to call getInteger on non-integer type'() {
		when:
		value.getInteger()

		then:
		thrown(UnexpectedTypeException)

		where:
		value << [
				new FloatingPointValue(1.0),
				new BooleanValue(true),
				new StringValue("hello"),
				new ComparatorValue(mockFunction, Comparator.comparingInt(Value::getInteger)),
				new MapValue(mockMapType, Map.of()),
				new TupleValue(mockTupleType, Map.of("a", new StringValue("abc"))),
				new IterableValue(mockIterableType, List.of())
		]
	}

	def 'Should throw an exception when trying to call isBool on non-bool type'() {
		when:
		value.isBool()

		then:
		thrown(UnexpectedTypeException)

		where:
		value << [
				new IntegerValue(1),
				new FloatingPointValue(1.0),
				new StringValue("hello"),
				new ComparatorValue(mockFunction, Comparator.comparingInt(Value::getInteger)),
				new MapValue(mockMapType, Map.of()),
				new TupleValue(mockTupleType, Map.of("a", new StringValue("abc"))),
				new IterableValue(mockIterableType, List.of())
		]
	}

	def 'Should throw an exception when trying to call getFloatingPoint on non-floating point type'() {
		when:
		value.getFloatingPoint()

		then:
		thrown(UnexpectedTypeException)

		where:
		value << [
				new IntegerValue(1),
				new BooleanValue(true),
				new StringValue("hello"),
				new ComparatorValue(mockFunction, Comparator.comparingInt(Value::getInteger)),
				new MapValue(mockMapType, Map.of()),
				new TupleValue(mockTupleType, Map.of("a", new StringValue("abc"))),
				new IterableValue(mockIterableType, List.of())
		]
	}

	def 'Should throw an exception when trying to call getString on non-string type'() {
		when:
		value.getString()

		then:
		thrown(UnexpectedTypeException)

		where:
		value << [
				new IntegerValue(1),
				new FloatingPointValue(1.0),
				new BooleanValue(true),
				new ComparatorValue(mockFunction, Comparator.comparingInt(Value::getInteger)),
				new MapValue(mockMapType, Map.of()),
				new TupleValue(mockTupleType, Map.of("a", new StringValue("abc"))),
				new IterableValue(mockIterableType, List.of())
		]
	}

	def 'Should throw an exception when trying to call getComparator on non-comparator type'() {
		when:
		value.getComparator()

		then:
		thrown(UnexpectedTypeException)

		where:
		value << [
				new IntegerValue(1),
				new FloatingPointValue(1.0),
				new BooleanValue(true),
				new StringValue("hello"),
				new MapValue(mockMapType, Map.of()),
				new TupleValue(mockTupleType, Map.of("a", new StringValue("abc"))),
				new IterableValue(mockIterableType, List.of())
		]
	}

	def 'Should throw an exception when trying to call findMethod on non-map type'() {
		when:
		value.findMethod("test")

		then:
		thrown(UnexpectedTypeException)

		where:
		value << [
				new IntegerValue(1),
				new FloatingPointValue(1.0),
				new BooleanValue(true),
				new StringValue("hello"),
				new ComparatorValue(mockFunction, Comparator.comparingInt(Value::getInteger)),
				new TupleValue(mockTupleType, Map.of("a", new StringValue("abc"))),
				new IterableValue(mockIterableType, List.of())
		]
	}

	def 'Should throw an exception when trying to call hasNext on non-iterable type'() {
		when:
		value.hasNext()

		then:
		thrown(UnexpectedTypeException)

		where:
		value << [
				new IntegerValue(1),
				new FloatingPointValue(1.0),
				new BooleanValue(true),
				new StringValue("hello"),
				new ComparatorValue(mockFunction, Comparator.comparingInt(Value::getInteger)),
				new MapValue(mockMapType, Map.of()),
				new TupleValue(mockTupleType, Map.of("a", new StringValue("abc"))),
		]
	}

	def 'Should throw an exception when trying to call next on non-iterable type'() {
		when:
		value.next()

		then:
		thrown(UnexpectedTypeException)

		where:
		value << [
				new IntegerValue(1),
				new FloatingPointValue(1.0),
				new BooleanValue(true),
				new StringValue("hello"),
				new ComparatorValue(mockFunction, Comparator.comparingInt(Value::getInteger)),
				new MapValue(mockMapType, Map.of()),
				new TupleValue(mockTupleType, Map.of("a", new StringValue("abc"))),
		]
	}

	def 'Should throw an exception when trying to get tuple element of non-tuple type'() {
		when:
		value.getTupleElement("a")

		then:
		thrown(UnexpectedTypeException)

		where:
		value << [
				new IntegerValue(1),
				new FloatingPointValue(1.0),
				new BooleanValue(true),
				new StringValue("hello"),
				new ComparatorValue(mockFunction, Comparator.comparingInt(Value::getInteger)),
				new MapValue(mockMapType, Map.of()),
				new IterableValue(mockIterableType, List.of())
		]
	}

	def 'Should throw an exception when trying to call compareTo on non-comparable type'() {
		when:
		value.compareTo(value)

		then:
		thrown(UnsupportedCastException)

		where:
		value << [
				new BooleanValue(true),
				new StringValue("hello"),
				new ComparatorValue(mockFunction, Comparator.comparingInt(Value::getInteger)),
				new MapValue(mockMapType, Map.of()),
				new TupleValue(mockTupleType, Map.of("a", new StringValue("abc"))),
				new IterableValue(mockIterableType, List.of())
		]
	}
}
