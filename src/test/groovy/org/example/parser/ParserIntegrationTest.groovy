package org.example.parser

import org.example.ast.Program
import org.example.ast.Statement
import org.example.ast.ValueType
import org.example.ast.expression.Argument
import org.example.ast.expression.BlockStatement
import org.example.ast.expression.ExplicitCastExpression
import org.example.ast.expression.FunctionCallExpression
import org.example.ast.expression.IdentifierExpression
import org.example.ast.expression.MapExpression
import org.example.ast.expression.MethodCallExpression
import org.example.ast.expression.SelectExpression
import org.example.ast.expression.TupleCallExpression
import org.example.ast.expression.TupleExpression
import org.example.ast.expression.arithmetic.AddArithmeticExpression
import org.example.ast.expression.arithmetic.DivideArithmeticExpression
import org.example.ast.expression.arithmetic.MultiplyArithmeticExpression
import org.example.ast.expression.arithmetic.SubtractArithmeticExpression
import org.example.ast.expression.logical.AndLogicalExpression
import org.example.ast.expression.logical.OrLogicalExpression
import org.example.ast.statement.AssignmentStatement
import org.example.ast.statement.DeclarationStatement
import org.example.ast.statement.ForStatement
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.ast.statement.IfStatement
import org.example.ast.statement.ReturnStatement
import org.example.ast.statement.WhileStatement
import org.example.ast.type.BooleanValue
import org.example.ast.type.FloatingPointValue
import org.example.ast.type.IntegerValue
import org.example.ast.type.StringValue
import org.example.ast.type.TypeDeclaration
import org.example.error.ErrorHandler
import org.example.lexer.LexerImpl
import org.example.parser.error.CriticalParserException
import org.example.parser.error.ParserException
import org.example.parser.error.UnexpectedTokenException
import org.example.token.Position
import spock.lang.Shared
import spock.lang.Specification

class ParserIntegrationTest extends Specification {

	@Shared
	var position = Position.builder().characterNumber(1).line(1).build()

	Parser toParser(String content) {
		var errorHandler = Mock(ErrorHandler)
		return toParser(content, errorHandler)
	}

	Parser toParser(String content, ErrorHandler errorHandler) {
		var reader = new StringReader(content)
		var lexer = new LexerImpl(reader, errorHandler)
		return new ParserImpl(lexer, errorHandler)
	}

	Program wrapStatements(List<Statement> statements) {
		return new Program(
				Map.of(
						"main",
						new FunctionDefinitionStatement(
								"main",
								List.of(),
								new TypeDeclaration(ValueType.VOID),
								new BlockStatement(statements, position),
								position
						)
				),
				Map.of()
		)
	}

	def 'Should be able to parse empty program'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program || result
		" "     || new Program(Map.of(), Map.of())
		";"     || new Program(Map.of(), Map.of())
		"; ; ;" || new Program(Map.of(), Map.of())
	}

	def 'Should be able to parse basic function definition'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                          || result
		"fun main() {}"                  || wrapStatements(List.of())
		"fun main() {;;}"                || wrapStatements(List.of())
		"fun main() {func();}"           || wrapStatements(List.of(new FunctionCallExpression("func", List.of(), position)))
		"fun main() {func1(); func2();}" || wrapStatements(List.of(new FunctionCallExpression("func1", List.of(), position), new FunctionCallExpression("func2", List.of(), position)))
		"fun main(): int {return 1;}"    || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.INTEGER), new BlockStatement(List.of(new ReturnStatement(new IntegerValue(1, position), position)), position), position)), Map.of())
		"fun main(a: int) {}"            || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(new Argument("a", new TypeDeclaration(ValueType.INTEGER))), new TypeDeclaration(ValueType.VOID), new BlockStatement(List.of(), position), position)), Map.of())
		"fun main(a: int, b: int) {}"    || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new Argument("b", new TypeDeclaration(ValueType.INTEGER))), new TypeDeclaration(ValueType.VOID), new BlockStatement(List.of(), position), position)), Map.of())
	}

	def 'Should be able to parse basic declaration statement'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                                        || result
		"int a = 1;"                                                   || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new IntegerValue(1, position), position)))
		"int a = b;"                                                   || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new IdentifierExpression("b", position), position)))
		"double a = 1.0;"                                              || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.FLOATING_POINT)), new FloatingPointValue(1.0, position), position)))
		"boolean a = true;"                                            || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new BooleanValue(true, position), position)))
		"String a = 'b';"                                              || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.STRING)), new StringValue("b", position), position)))
		"Comparator<int> a = func;"                                    || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.COMPARATOR, List.of(new TypeDeclaration(ValueType.INTEGER)))), new IdentifierExpression("func", position), position)))
		"Tuple<String> a = |b AS c|;"                                  || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.TUPLE, List.of(new TypeDeclaration(ValueType.STRING)))), new TupleExpression(Map.of("c", new IdentifierExpression("b", position)), position), position)))
		"Tuple<String, int> a = |b AS c, d AS e|;"                     || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.TUPLE, List.of(new TypeDeclaration(ValueType.STRING), new TypeDeclaration(ValueType.INTEGER)))), new TupleExpression(Map.of("c", new IdentifierExpression("b", position), "e", new IdentifierExpression("d", position)), position), position)))
		"Tuple<String, int> a = (|b AS c, d AS e|);"                   || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.TUPLE, List.of(new TypeDeclaration(ValueType.STRING), new TypeDeclaration(ValueType.INTEGER)))), new TupleExpression(Map.of("c", new IdentifierExpression("b", position), "e", new IdentifierExpression("d", position)), position), position)))
		"Map<int, String> a = [];"                                     || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.MAP, List.of(new TypeDeclaration(ValueType.INTEGER), new TypeDeclaration(ValueType.STRING)))), new MapExpression(Map.of(), position), position)))
		"Map<int, String> a = [b : c];"                                || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.MAP, List.of(new TypeDeclaration(ValueType.INTEGER), new TypeDeclaration(ValueType.STRING)))), new MapExpression(Map.of(new IdentifierExpression("b", position), new IdentifierExpression("c", position)), position), position)))
		"Map<int, String> a = [b : c, d : e];"                         || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.MAP, List.of(new TypeDeclaration(ValueType.INTEGER), new TypeDeclaration(ValueType.STRING)))), new MapExpression(Map.of(new IdentifierExpression("b", position), new IdentifierExpression("c", position), new IdentifierExpression("d", position), new IdentifierExpression("e", position)), position), position)))
		"Map<int, String> a = [b : c, d : []];"                        || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.MAP, List.of(new TypeDeclaration(ValueType.INTEGER), new TypeDeclaration(ValueType.STRING)))), new MapExpression(Map.of(new IdentifierExpression("b", position), new IdentifierExpression("c", position), new IdentifierExpression("d", position), new MapExpression(Map.of(), position)), position), position)))
		"Iterable<int> a = SELECT entry.key AS key FROM map AS entry;" || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.ITERABLE, List.of(new TypeDeclaration(ValueType.INTEGER)))), new SelectExpression(new TupleExpression(Map.of("key", new TupleCallExpression(new IdentifierExpression("entry", position), "key", position)), position), Map.entry("entry", new IdentifierExpression("map", position)), List.of(), new BooleanValue(true, position), List.of(), new BooleanValue(true, position), List.of(), position), position)))
	}

	def 'Should be able to parse while loop'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                                 || result
		"fun main() {boolean a = true; while a {a = false;}}"   || wrapStatements(List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new BooleanValue(true, position), position), new WhileStatement(new IdentifierExpression("a", position), new BlockStatement(List.of(new AssignmentStatement("a", new BooleanValue(false, position), position)), position), position)))
		"fun main() {boolean a = true; while (a) {a = false;}}" || wrapStatements(List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new BooleanValue(true, position), position), new WhileStatement(new IdentifierExpression("a", position), new BlockStatement(List.of(new AssignmentStatement("a", new BooleanValue(false, position), position)), position), position)))
	}

	def 'Should be able to parse if statement'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                              || result
		"fun main() {boolean a = true; if a {a = false;}}"   || wrapStatements(List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new BooleanValue(true, position), position), new IfStatement(new IdentifierExpression("a", position), new BlockStatement(List.of(new AssignmentStatement("a", new BooleanValue(false, position), position)), position), new BlockStatement(List.of(), position), position)))
		"fun main() {boolean a = true; if (a) {a = false;}}" || wrapStatements(List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new BooleanValue(true, position), position), new IfStatement(new IdentifierExpression("a", position), new BlockStatement(List.of(new AssignmentStatement("a", new BooleanValue(false, position), position)), position), new BlockStatement(List.of(), position), position)))
	}

	def 'Should be able to parse for statement'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                              || result
		"fun main() {for (Tuple<String> a : b) {print(a);}}" || wrapStatements(List.of(new ForStatement(new Argument("a", new TypeDeclaration(ValueType.TUPLE, List.of(new TypeDeclaration(ValueType.STRING)))), new IdentifierExpression("b", position), new BlockStatement(List.of(new FunctionCallExpression("print", List.of(new IdentifierExpression("a", position)), position)), position), position)))
	}

	def 'Should be able to parse assignment statement'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                    || result
		"fun main() {a = b or c;}" || wrapStatements(List.of(new AssignmentStatement("a", new OrLogicalExpression(new IdentifierExpression("b", position), new IdentifierExpression("c", position), position), position)))
	}

	def 'Should be able to parse single expression statement'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                       || result
		"fun main() {functionCall();}"                || wrapStatements(List.of(new FunctionCallExpression("functionCall", List.of(), position)))
		"fun main() {i[mapCall];}"                    || wrapStatements(List.of(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("operator[]", List.of(new IdentifierExpression("mapCall", position)), position), position)))
		"fun main() {i[mapCall1][mapCall2];}"         || wrapStatements(List.of(new MethodCallExpression(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("operator[]", List.of(new IdentifierExpression("mapCall1", position)), position), position), new FunctionCallExpression("operator[]", List.of(new IdentifierExpression("mapCall2", position)), position), position)))
		"fun main() {i[mapCall1].methodCall();}"      || wrapStatements(List.of(new MethodCallExpression(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("operator[]", List.of(new IdentifierExpression("mapCall1", position)), position), position), new FunctionCallExpression("methodCall", List.of(), position), position)))
		"fun main() {i.methodCall();}"                || wrapStatements(List.of(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("methodCall", List.of(), position), position)))
		"fun main() {i.methodCall1().methodCall2();}" || wrapStatements(List.of(new MethodCallExpression(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("methodCall1", List.of(), position), position), new FunctionCallExpression("methodCall2", List.of(), position), position)))
		"fun main() {i.methodCall1().tupleCall2;}"    || wrapStatements(List.of(new TupleCallExpression(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("methodCall1", List.of(), position), position), "tupleCall2", position)))
		"fun main() {i.tupleCall1.methodCall2();}"    || wrapStatements(List.of(new MethodCallExpression(new TupleCallExpression(new IdentifierExpression("i", position), "tupleCall1", position), new FunctionCallExpression("methodCall2", List.of(), position), position)))
		"fun main() {i.tupleCall;}"                   || wrapStatements(List.of(new TupleCallExpression(new IdentifierExpression("i", position), "tupleCall", position)))
		"fun main() {i.tupleCall1.tupleCall2;}"       || wrapStatements(List.of(new TupleCallExpression(new TupleCallExpression(new IdentifierExpression("i", position), "tupleCall1", position), "tupleCall2", position)))
	}

	def 'Should be able to parse return statement'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                           || result
		"fun main(): int {return 1 + 2;}" || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.INTEGER), new BlockStatement(List.of(new ReturnStatement(new AddArithmeticExpression(new IntegerValue(1, position), new IntegerValue(2, position), position), position)), position), position)), Map.of())
		"fun main(): int {return 1 - 2;}" || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.INTEGER), new BlockStatement(List.of(new ReturnStatement(new SubtractArithmeticExpression(new IntegerValue(1, position), new IntegerValue(2, position), position), position)), position), position)), Map.of())
		"fun main(): int {return 1 / 2;}" || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.INTEGER), new BlockStatement(List.of(new ReturnStatement(new DivideArithmeticExpression(new IntegerValue(1, position), new IntegerValue(2, position), position), position)), position), position)), Map.of())
		"fun main(): int {return 1 * 2;}" || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.INTEGER), new BlockStatement(List.of(new ReturnStatement(new MultiplyArithmeticExpression(new IntegerValue(1, position), new IntegerValue(2, position), position), position)), position), position)), Map.of())
	}

	def 'Should be able to parse nested block statement'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                          || result
		"fun main() {{functionCall();}}" || wrapStatements(List.of(new BlockStatement(List.of(new FunctionCallExpression("functionCall", List.of(), position)), position)))
	}

	def 'Should be able to perform mathematical operations'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                           || result
		"int a = 1 + 2 * 3;"              || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1, position), new MultiplyArithmeticExpression(new IntegerValue(2, position), new IntegerValue(3, position), position), position), position)))
		"int a = (1 + 2) * 3;"            || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new MultiplyArithmeticExpression(new AddArithmeticExpression(new IntegerValue(1, position), new IntegerValue(2, position), position), new IntegerValue(3, position), position), position)))
		"int a = 1 + i * 3;"              || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1, position), new MultiplyArithmeticExpression(new IdentifierExpression("i", position), new IntegerValue(3, position), position), position), position)))
		"int a = 1 + i.tupleCall * 3;"    || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1, position), new MultiplyArithmeticExpression(new TupleCallExpression(new IdentifierExpression("i", position), "tupleCall", position), new IntegerValue(3, position), position), position), position)))
		"int a = 1 + i[mapCall] * 3;"     || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1, position), new MultiplyArithmeticExpression(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("operator[]", List.of(new IdentifierExpression("mapCall", position)), position), position), new IntegerValue(3, position), position), position), position)))
		"int a = 1 + i.methodCall() * 3;" || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1, position), new MultiplyArithmeticExpression(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("methodCall", List.of(), position), position), new IntegerValue(3, position), position), position), position)))
		"int a = 1 + functionCall() * 3;" || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1, position), new MultiplyArithmeticExpression(new FunctionCallExpression("functionCall", List.of(), position), new IntegerValue(3, position), position), position), position)))
		"double a = (@double 1) + 2.0;"   || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.FLOATING_POINT)), new AddArithmeticExpression(new ExplicitCastExpression(new TypeDeclaration(ValueType.FLOATING_POINT), new IntegerValue(1, position), position), new FloatingPointValue(2, position), position), position)))
	}

	def 'Should be able to perform logical operations'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                  || result
		"boolean a = false or true and false;"   || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new OrLogicalExpression(new BooleanValue(false, position), new AndLogicalExpression(new BooleanValue(true, position), new BooleanValue(false, position), position), position), position)))
		"boolean a = (false or true) and false;" || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new AndLogicalExpression(new OrLogicalExpression(new BooleanValue(false, position), new BooleanValue(true, position), position), new BooleanValue(false, position), position), position)))
		"boolean a = false or b and false;"      || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new OrLogicalExpression(new BooleanValue(false, position), new AndLogicalExpression(new IdentifierExpression("b", position), new BooleanValue(false, position), position), position), position)))
		"boolean a = not false;"                 || new Program(Map.of(), Map.of("a", new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new BooleanValue(true, position), position)))
	}

	// ERRORS

	def 'Should throw critical exception when did not find necessary expression'() {
		given:
		var errorHandler = Mock(ErrorHandler)
		var parser = toParser(program, errorHandler)

		when:
		parser.parseProgram()

		then:
		1 * errorHandler.handleParserException(_ as CriticalParserException)
		thrown(CriticalParserException)

		where:
		program << [
				"int a =",
				"int a =;",
				"int a = 1 +;",
				"int a = 1 -;",
				"int a = 1 *;",
				"int a = 1 /;",
				"int a = (1;",
				"Map<String, String> map = [a:b",
				"Tuple<String> tuple = |expression AS key",
				"boolean a = not;",
				"boolean a = b;boolean a = b;",
				"boolean a = true and;",
				"boolean a = true or;",
				"Map<String, String> map = [a:b,]",
				"Map<String, String> map = [a:]",
				"Map<String, String> map = [a]",
				"Tuple<String> tuple = |expression AS |",
				"Tuple<String, String> tuple = |expression AS key,",
				"Tuple<String> tuple = |expression key|",
				"Tuple<String> tuple = | AS key|",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + ;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value >  ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int, int> iterable = SELECT db1.value + db2.value AS value, FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int, int> iterable = SELECT;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"fun main(: int) {}",
				"fun main() {",
				"fun main() {}fun main() {}",
				"fun main() {(}",
				"fun main() {[}",
				"fun main() {|}",
				"fun main(a: int, : int) {}",
				"fun main() {while {}}",
				"fun main() {while () {}}",
				"fun main() {while ) {}}",
				"fun main() {while (true {}}",
				"fun main() {if (true) {}",
				"fun main() {(true) {} else {}}",
				"fun main() {if (true) { else {}}",
				"fun main() {if (true) {} else {}",
				"fun main() {if true) {}}",
				"fun main() {if () {}}",
				"fun main() {if (true {}}",
				"fun main() {if true) {} else {}}",
				"fun main() {if () {} else {}}",
				"fun main() {if (true {} else {}}",
				"fun main() {if (true) } else {}}",
				"fun main() {(int a: expression) {expression;}}",
				"fun main() {for (int a: expression {expression;}}",
				"fun main() {for int a: expression) {expression;}}",
				"fun main() {for (a: expression) {expression;}}",
				"fun main() {for (int: expression) {expression;}}",
				"fun main() {for (int a expression) {expression;}}",
				"fun main() {for (int a:) {expression;}}",
				"fun main() {for (int a: expression) {expression;}",
				"fun main() {a =;}",
				"fun main() {a + b = expression;}",
		]
	}

	def 'Should raise an exception when a enclosing token is expected'() {
		given:
		var errorHandler = Mock(ErrorHandler)
		var parser = toParser(program, errorHandler)

		when:
		parser.parseProgram()

		then:
		(1.._) * errorHandler.handleParserException(_ as UnexpectedTokenException)
		noExceptionThrown()

		where:
		program << [
				"identifier;",
				"int a = 1",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUPBY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key == db2.key db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 ON db1.key db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 JOIN map2 AS db2 db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"Iterable<int> iterable = SELECT db1.value + db2.value AS value FROM map1 AS db1 map2 AS db2 ON db1.key == db2.key WHERE db1.value > 2 GROUP BY value HAVING value > 0 ORDER BY db1.key DESC, db2.key ASC, db1.key + db2.key;",
				"fun main() {(true) {}}",
				"fun main() {for (int a: expression) expression;}}",
				"fun main() {a = expression}",
		]
	}

	def 'Should raise an exception when expecting type declaration on function'() {
		given:
		var errorHandler = Mock(ErrorHandler)
		var parser = toParser(program, errorHandler)

		when:
		parser.parseProgram()

		then:
		1 * errorHandler.handleParserException(_ as ParserException)
		noExceptionThrown()

		where:
		program << [
				"fun main():{}",
				"fun main(a: int, b: int):{}",
				"fun main(a: int, b: int):int",
				"fun main(a: int, b: int)",
				"fun main() {while(true)}",
				"fun main() {for (int a: expression)}",
		]
	}
}
