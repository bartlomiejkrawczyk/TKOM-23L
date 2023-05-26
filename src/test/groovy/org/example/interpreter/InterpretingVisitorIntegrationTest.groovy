package org.example.interpreter

import java.nio.charset.StandardCharsets
import org.example.error.ErrorHandler
import org.example.interpreter.error.InterpreterException
import org.example.lexer.CommentFilterLexer
import org.example.lexer.LexerImpl
import org.example.parser.ParserImpl
import spock.lang.Specification

class InterpretingVisitorIntegrationTest extends Specification {

	String interpret(String file) {
		var errorHandler = Mock(ErrorHandler)
		return interpret(file, errorHandler)
	}

	String interpret(String file, ErrorHandler errorHandler) {
		var reader = new InputStreamReader(getClass().getResourceAsStream("/$file"))
		var lexer = new LexerImpl(reader, errorHandler)
		var commentFiler = new CommentFilterLexer(lexer)
		var parser = new ParserImpl(commentFiler, errorHandler)
		var program = parser.parseProgram()
//		println(program.print())
		var output = new ByteArrayOutputStream()
		final String utf8 = StandardCharsets.UTF_8.name()
		try (var out = new PrintStream(output, true, utf8)) {
			var visitor = new InterpretingVisitor(errorHandler, out, program)
			visitor.execute()
		}
		return output.toString(utf8)
	}

	String transform(String text) {
		return text.replaceAll("\r", "")
	}

	def 'Should evaluate program correctly'() {
		expect:
		transform(interpret(program)) == result

		where:
		program                               || result
		"hello.txt"                           || "Hello, World!\n"
		"math.txt"                            || "10\n10\n108\n10\n2.5\n-123.25\n617.75\n20\n-120.75\n0\n125.75\n100\n-308.125\n1\n-0.02028397565922921\n-10\n-2.5\n"
		"logic.txt"                           || "true\nfalse\ntrue\ntrue\nfalse\ntrue\nfalse\ntrue\ntrue\nfalse\ntrue\nfalse\nfalse\n"
		"relation.txt"                        || "true\ntrue\nfalse\ntrue\nfalse\nfalse\ntrue\ntrue\nfalse\ntrue\nfalse\nfalse\nfalse\nfalse\nfalse\ntrue\ntrue\ntrue\nfalse\nfalse\nfalse\ntrue\ntrue\ntrue\ntrue\ntrue\ntrue\nfalse\nfalse\nfalse\nfalse\ntrue\n"
		"visibility.txt"                      || "2\n1\n3\n2\n1\n4\n1\n5\n2\n"
		"if.txt"                              || "if\nelse if\nelse\n"
		"while.txt"                           || "0\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n8\n6\n4\n2\n"
		"map.txt"                             || "false\ntrue\nid_1\nid_2\nid_3\nid_4\nid_4\nid_3\nid_2\n"
		"tuple.txt"                           || "abc\n1\ntrue\n"
		"sql.txt"                             || "JOIN + ORDER BY\n1\n9\n\n1\n9\n\n1\n9\n\n1\n9\n\n2\n9\n\n2\n9\n\n3\n9\n\n3\n9\n\n4\n9\n\n4\n9\n\n5\n9\n\n5\n9\n\n6\n9\n\n6\n9\n\n7\n9\n\n7\n9\n\n8\n9\n\n8\n9\n\n9\n9\n\n9\n9\n\n1\n8\n\n1\n8\n\n2\n8\n\n3\n8\n\n4\n8\n\n5\n8\n\n6\n8\n\n7\n8\n\n8\n8\n\n9\n8\n\n1\n7\n\n1\n7\n\n2\n7\n\n3\n7\n\n4\n7\n\n5\n7\n\n6\n7\n\n7\n7\n\n8\n7\n\n9\n7\n\n1\n6\n\n1\n6\n\n2\n6\n\n3\n6\n\n4\n6\n\n5\n6\n\n6\n6\n\n7\n6\n\n8\n6\n\n9\n6\n\n1\n5\n\n1\n5\n\n2\n5\n\n3\n5\n\n4\n5\n\n5\n5\n\n6\n5\n\n7\n5\n\n8\n5\n\n9\n5\n\n1\n4\n\n1\n4\n\n2\n4\n\n3\n4\n\n4\n4\n\n5\n4\n\n6\n4\n\n7\n4\n\n8\n4\n\n9\n4\n\n1\n3\n\n1\n3\n\n2\n3\n\n3\n3\n\n4\n3\n\n5\n3\n\n6\n3\n\n7\n3\n\n8\n3\n\n9\n3\n\n1\n2\n\n1\n2\n\n2\n2\n\n3\n2\n\n4\n2\n\n5\n2\n\n6\n2\n\n7\n2\n\n8\n2\n\n9\n2\n\n1\n1\n\n1\n1\n\n2\n1\n\n3\n1\n\n4\n1\n\n5\n1\n\n6\n1\n\n7\n1\n\n8\n1\n\n9\n1\n\nJOIN ON + ORDER BY\n9\n1\n\n8\n2\n\n7\n3\n\n6\n4\n\n5\n5\n\n4\n6\n\n3\n7\n\n2\n8\n\n1\n9\n\n1\n9\n\nJOIN ON + WHERE + ORDER BY\n9\n1\n\n8\n2\n\n7\n3\n\n6\n4\n\nJOIN ON + GROUP BY + ORDER BY\n9\n1\n\n8\n2\n\n7\n3\n\n6\n4\n\n5\n5\n\n4\n6\n\n3\n7\n\n2\n8\n\n1\n9\n\nJOIN ON + GROUP BY + HAVING + ORDER BY\n9\n1\n\n1\n9\n\n"
		"nestedSql.txt"                       || "1.0\n9.0\n\n1.0\n9.0\n\n2.0\n8.0\n\n3.0\n7.0\n\n4.0\n6.0\n\n5.0\n5.0\n\n6.0\n4.0\n\n7.0\n3.0\n\n8.0\n2.0\n\n9.0\n1.0\n\n"
		"explicit-cast.txt"                   || "true\ntrue\ntrue\nfalse\n1\n1\n1\n0\n1.0\n1.0\n1.0\n0.0\n"
		"empty-collection-type-deduction.txt" || "1\nend\n"
	}

	def 'When an invalid program is run, should show exceptions without interpreter throwing one'() {
		given:
		var errorHandler = Mock(ErrorHandler)

		when:
		println(interpret(program, errorHandler))

		then:
		noExceptionThrown()
		1 * errorHandler.handleInterpreterException(_ as InterpreterException)

		where:
		program << [
				"error-duplicate-variable.txt",
				"error-no-such-variable.txt",
				"error-recursion-limit.txt",
				"error-compare-not-comparable.txt",
				"error-equality-for-non-equal.txt",
				"error-arithmetic-operation-on-non-numeric-type.txt",
				"error-negation-on-non-numeric-type.txt",
				"error-tuple-call-on-non-tuple.txt",
				"error-method-call-on-not-a-map.txt",
				"error-function-call-with-different-size-arguments.txt",
				"error-void-function-returned-value.txt",
				"error-function-did-not-return-value-even-though-it-should.txt",
				"error-undefined-explicit-cast.txt",
				"error-unsupported-cast-from-given-type.txt",
				"error-iterable-called-on-non-iterable-type.txt",
				"error-assign-different-type-value-to-variable.txt",
				"error-no-such-tuple-element.txt",
		]
	}
}
