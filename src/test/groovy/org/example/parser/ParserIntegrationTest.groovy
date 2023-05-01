package org.example.parser

import org.example.ast.Program
import org.example.ast.ValueType
import org.example.ast.expression.*
import org.example.ast.statement.DeclarationStatement
import org.example.ast.statement.FunctionDefinitionStatement
import org.example.ast.type.*
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
	}

	def 'Should be able to parse basic function definition'() {
		given:
		var parser = toParser(program)

		expect:
		parser.parseProgram() == result

		where:
		program                                    || result
		"fun main() {}"                            || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of()))), List.of())
		"fun main() {func();}"                     || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of(new FunctionCallExpression("func", List.of()))))), List.of())
		"fun main(): int {}"                       || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(), new TypeDeclaration(ValueType.INTEGER), new BlockExpression(List.of()))), List.of())
		"fun main(a: int) {}"                      || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(new Argument("a", new TypeDeclaration(ValueType.INTEGER))), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of()))), List.of())
		"fun main(a: int, b: int) {}"              || new Program(Map.of("main", new FunctionDefinitionStatement("main", List.of(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new Argument("b", new TypeDeclaration(ValueType.INTEGER))), new TypeDeclaration(ValueType.VOID), new BlockExpression(List.of()))), List.of())
		"int a = 1;"                               || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new IntegerValue(1))))
		"int a = b;"                               || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.INTEGER)), new IdentifierExpression("b"))))
		"double a = 1.0;"                          || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.FLOATING_POINT)), new FloatingPointValue(1.0))))
		"boolean a = true;"                        || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.FLOATING_POINT)), new BooleanValue(true))))
		"String a = 'b';"                          || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.STRING)), new StringValue("b"))))
		"Tuple<String> a = b AS c;"                || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.TUPLE, List.of(new TypeDeclaration(ValueType.STRING)))), new TupleExpression(Map.of("c", new IdentifierExpression("b"))))))
		"Tuple<String, int> a = b AS c, d AS e;"   || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.TUPLE, List.of(new TypeDeclaration(ValueType.STRING)))), new TupleExpression(Map.of("c", new IdentifierExpression("b"), "e", new IdentifierExpression("d"))))))
		"Tuple<String, int> a = (b AS c, d AS e);" || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.TUPLE, List.of(new TypeDeclaration(ValueType.STRING)))), new TupleExpression(Map.of("c", new IdentifierExpression("b"), "e", new IdentifierExpression("d"))))))
		"Map<int, String> a = {};"                 || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.MAP, List.of(new TypeDeclaration(ValueType.INTEGER), new TypeDeclaration(ValueType.STRING)))), new MapExpression(Map.of()))))
		"Map<int, String> a = {b : c};"            || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.MAP, List.of(new TypeDeclaration(ValueType.INTEGER), new TypeDeclaration(ValueType.STRING)))), new MapExpression(Map.of(new IdentifierExpression("b"), new IdentifierExpression("c"))))))
		"Map<int, String> a = {b : c, d : e};"     || new Program(Map.of(), List.of(new DeclarationStatement(new Argument("a", new TypeDeclaration(ValueType.MAP, List.of(new TypeDeclaration(ValueType.INTEGER), new TypeDeclaration(ValueType.STRING)))), new MapExpression(Map.of(new IdentifierExpression("b"), new IdentifierExpression("c"), new IdentifierExpression("d"), new IdentifierExpression("e"))))))
	}
}
