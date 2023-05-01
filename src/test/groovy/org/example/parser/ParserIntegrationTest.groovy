package org.example.parser

import org.example.ast.Program
import org.example.ast.ValueType
import org.example.ast.expression.Argument
import org.example.ast.expression.BlockExpression
import org.example.ast.expression.FunctionCallExpression
import org.example.ast.expression.IdentifierExpression
import org.example.ast.expression.MapExpression
import org.example.ast.expression.MethodCallExpression
import org.example.ast.expression.SelectExpression
import org.example.ast.expression.TupleCallExpression
import org.example.ast.expression.TupleExpression
import org.example.ast.expression.arithmetic.AddArithmeticExpression
import org.example.ast.expression.arithmetic.MultiplyArithmeticExpression
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
import spock.lang.Specification

class ParserIntegrationTest extends Specification {

	Parser toParser(String content) {
		var reader = new StringReader(content)
		var errorHandler = Mock(ErrorHandler)
		var lexer = new LexerImpl(reader, errorHandler)
		return new ParserImpl(lexer, errorHandler)
	}

	def 'Should be able to parse empty program'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program || result
		" "     || new Program(Map.of(), List.of())
		";"     || new Program(Map.of(), List.of())
		"; ; ;" || new Program(Map.of(), List.of())
	}

	def 'Should be able to parse basic function definition'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                          || result
		"fun main() {}"                  || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of()))), List.of())
		"fun main() {;;}" || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of()))), List.of())
		"fun main() {func();}"           || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of(new FunctionCallExpression("func", List.of()))))), List.of())
		"fun main() {func1(); func2();}" || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of(new FunctionCallExpression("func1", List.of()), new FunctionCallExpression("func2", List.of()))))), List.of())
		"fun main(): int {return 1;}"    || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.INTEGER), new BlockExpression(List.of(new ReturnStatement(new IntegerValue(1)))))), List.of())
		"fun main(a: int) {}"            || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(new Argument("a", new TypeDeclaration(ValueType.INTEGER))), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of()))), List.of())
		"fun main(a: int, b: int) {}"    || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new Argument("b", new TypeDeclaration(ValueType.INTEGER))), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of()))), List.of())
	}

	def 'Should be able to parse basic declaration statement'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                                        || result
		"int a = 1;"                                                   || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new IntegerValue(1))))
		"int a = b;"                                                   || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new IdentifierExpression("b"))))
		"double a = 1.0;"                                              || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.FLOATING_POINT)), new FloatingPointValue(1.0))))
		"boolean a = true;"                                            || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new BooleanValue(true))))
		"String a = 'b';"                                              || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.STRING)), new StringValue("b"))))
		"Comparator<int> a = func;"                                    || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.COMPARATOR, List.of(new TypeDeclaration(ValueType.INTEGER)))), new IdentifierExpression("func"))))
		"Tuple<String> a = |b AS c|;"                                  || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.TUPLE, List.of(new TypeDeclaration(ValueType.STRING)))), new TupleExpression(Map.of("c", new IdentifierExpression("b"))))))
		"Tuple<String, int> a = |b AS c, d AS e|;"                     || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.TUPLE, List.of(new TypeDeclaration(ValueType.STRING), new TypeDeclaration(ValueType.INTEGER)))), new TupleExpression(Map.of("c", new IdentifierExpression("b"), "e", new IdentifierExpression("d"))))))
		"Tuple<String, int> a = (|b AS c, d AS e|);"                   || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.TUPLE, List.of(new TypeDeclaration(ValueType.STRING), new TypeDeclaration(ValueType.INTEGER)))), new TupleExpression(Map.of("c", new IdentifierExpression("b"), "e", new IdentifierExpression("d"))))))
		"Map<int, String> a = [];"                                     || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.MAP, List.of(new TypeDeclaration(ValueType.INTEGER), new TypeDeclaration(ValueType.STRING)))), new MapExpression(Map.of()))))
		"Map<int, String> a = [b : c];"                                || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.MAP, List.of(new TypeDeclaration(ValueType.INTEGER), new TypeDeclaration(ValueType.STRING)))), new MapExpression(Map.of(new IdentifierExpression("b"), new IdentifierExpression("c"))))))
		"Map<int, String> a = [b : c, d : e];"                         || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.MAP, List.of(new TypeDeclaration(ValueType.INTEGER), new TypeDeclaration(ValueType.STRING)))), new MapExpression(Map.of(new IdentifierExpression("b"), new IdentifierExpression("c"), new IdentifierExpression("d"), new IdentifierExpression("e"))))))
		"Iterable<int> a = SELECT entry.key AS key FROM map AS entry;" || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.ITERABLE, List.of(new TypeDeclaration(ValueType.INTEGER)))), new SelectExpression(new TupleExpression(Map.of("key", new TupleCallExpression(new IdentifierExpression("entry"), "key"))), Map.entry("entry", new IdentifierExpression("map")), List.of(), new BooleanValue(true), List.of(), new BooleanValue(true), List.of()))))
	}

	def 'Should be able to parse while loop'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                                 || result
		"fun main() {boolean a = true; while a {a = false;}}"   || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new BooleanValue(true)), new WhileStatement(new IdentifierExpression("a"), new BlockExpression(List.of(new AssignmentStatement("a", new BooleanValue(false))))))))), List.of())
		"fun main() {boolean a = true; while (a) {a = false;}}" || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new BooleanValue(true)), new WhileStatement(new IdentifierExpression("a"), new BlockExpression(List.of(new AssignmentStatement("a", new BooleanValue(false))))))))), List.of())
	}

	def 'Should be able to parse if statement'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                              || result
		"fun main() {boolean a = true; if a {a = false;}}"   || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new BooleanValue(true)), new IfStatement(new IdentifierExpression("a"), new BlockExpression(List.of(new AssignmentStatement("a", new BooleanValue(false)))), new BlockExpression(List.of())))))), List.of())
		"fun main() {boolean a = true; if (a) {a = false;}}" || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new BooleanValue(true)), new IfStatement(new IdentifierExpression("a"), new BlockExpression(List.of(new AssignmentStatement("a", new BooleanValue(false)))), new BlockExpression(List.of())))))), List.of())
	}

	def 'Should be able to parse for statement'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                              || result
		"fun main() {for (Tuple<String> a : b) {print(a);}}" || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of(new ForStatement(new Argument("a", new TypeDeclaration(ValueType.TUPLE, List.of(new TypeDeclaration(ValueType.STRING)))), new IdentifierExpression("b"), new BlockExpression(List.of(new FunctionCallExpression("print", List.of(new IdentifierExpression("a")))))))))), List.of())
	}

	def 'Should be able to parse assignment statement'() {
// TODO: add tests
	}

	def 'Should be able to parse single expression statement'() {

	}

	def 'Should be able to parse return statement'() {

	}

	def 'Should be able to parse nested block statement'() {

	}

	def 'Should be able to perform mathematical operations'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                           || result
		"int a = 1 + 2 * 3;"              || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1), new MultiplyArithmeticExpression(new IntegerValue(2), new IntegerValue(3))))))
		"int a = (1 + 2) * 3;"            || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new MultiplyArithmeticExpression(new AddArithmeticExpression(new IntegerValue(1), new IntegerValue(2)), new IntegerValue(3)))))
		"int a = 1 + i * 3;"              || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1), new MultiplyArithmeticExpression(new IdentifierExpression("i"), new IntegerValue(3))))))
		"int a = 1 + i.tupleCall * 3;"    || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1), new MultiplyArithmeticExpression(new TupleCallExpression(new IdentifierExpression("i"), "tupleCall"), new IntegerValue(3))))))
		"int a = 1 + i[mapCall] * 3;"     || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1), new MultiplyArithmeticExpression(new MethodCallExpression(new IdentifierExpression("i"), new FunctionCallExpression("operator[]", List.of(new IdentifierExpression("mapCall")))), new IntegerValue(3))))))
		"int a = 1 + i.methodCall() * 3;" || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1), new MultiplyArithmeticExpression(new MethodCallExpression(new IdentifierExpression("i"), new FunctionCallExpression("methodCall", List.of())), new IntegerValue(3))))))
		"int a = 1 + functionCall() * 3;" || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new AddArithmeticExpression(new IntegerValue(1), new MultiplyArithmeticExpression(new FunctionCallExpression("functionCall", List.of()), new IntegerValue(3))))))
	}

	def 'Should be able to perform logical operations'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                  || result
		"boolean a = false or true and false;"   || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new OrLogicalExpression(new BooleanValue(false), new AndLogicalExpression(new BooleanValue(true), new BooleanValue(false))))))
		"boolean a = (false or true) and false;" || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new AndLogicalExpression(new OrLogicalExpression(new BooleanValue(false), new BooleanValue(true)), new BooleanValue(false)))))
		"boolean a = false or b and false;"      || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.BOOLEAN)), new OrLogicalExpression(new BooleanValue(false), new AndLogicalExpression(new IdentifierExpression("b"), new BooleanValue(false))))))
	}
}
