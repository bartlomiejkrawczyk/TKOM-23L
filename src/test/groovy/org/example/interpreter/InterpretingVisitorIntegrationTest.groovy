package org.example.interpreter

import java.nio.charset.StandardCharsets
import org.example.error.ErrorHandler
import org.example.lexer.CommentFilterLexer
import org.example.lexer.LexerImpl
import org.example.parser.ParserImpl
import spock.lang.Specification

class InterpretingVisitorIntegrationTest extends Specification {

	String interpret(String file) {
		var errorHandler = Mock(ErrorHandler)
		var reader = new InputStreamReader(getClass().getResourceAsStream("/$file"))
		var lexer = new LexerImpl(reader, errorHandler)
		var commentFiler = new CommentFilterLexer(lexer)
		var parser = new ParserImpl(commentFiler, errorHandler)
		var program = parser.parseProgram()
//		println(program.print())
		var output = new ByteArrayOutputStream()
		final String utf8 = StandardCharsets.UTF_8.name()
		try (var out = new PrintStream(output, true, utf8)) {
			var visitor = new InterpretingVisitor(errorHandler, out)
			program.accept(visitor)
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
		program          || result
		"hello.txt"      || "Hello, World!\n"
		"math.txt"       || "10\n10\n108\n10\n2.5\n-123.25\n617.75\n20\n-120.75\n0\n125.75\n100\n-308.125\n1\n-0.02028397565922921\n-10\n-2.5\n"
		"logic.txt"      || "true\nfalse\ntrue\ntrue\nfalse\ntrue\nfalse\ntrue\ntrue\nfalse\ntrue\nfalse\nfalse\n"
		"relation.txt"   || "true\ntrue\nfalse\ntrue\nfalse\nfalse\ntrue\ntrue\nfalse\ntrue\nfalse\nfalse\nfalse\nfalse\nfalse\ntrue\ntrue\ntrue\nfalse\nfalse\nfalse\ntrue\ntrue\ntrue\ntrue\ntrue\ntrue\nfalse\nfalse\nfalse\nfalse\ntrue\n"
		"visibility.txt" || "2\n1\n3\n2\n1\n4\n1\n5\n2\n"
		"if.txt"         || "if\nelse if\nelse\n"
		"while.txt"      || "0\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n8\n6\n4\n2\n"
		"map.txt"        || "false\ntrue\nid_1\nid_2\nid_3\nid_4\nid_4\nid_3\nid_2\n"
		"tuple.txt"      || "abc\n1\ntrue\n"
		"sql.txt"        || "JOIN + ORDER BY\nkey_0\n1\n9\n\nkey_0\n1\n9\n\nkey_1\n1\n9\n\nkey_1\n1\n9\n\nkey_2\n2\n9\n\nkey_2\n2\n9\n\nkey_3\n3\n9\n\nkey_3\n3\n9\n\nkey_4\n4\n9\n\nkey_4\n4\n9\n\nkey_5\n5\n9\n\nkey_5\n5\n9\n\nkey_6\n6\n9\n\nkey_6\n6\n9\n\nkey_7\n7\n9\n\nkey_7\n7\n9\n\nkey_8\n8\n9\n\nkey_8\n8\n9\n\nkey_9\n9\n9\n\nkey_9\n9\n9\n\nkey_0\n1\n8\n\nkey_1\n1\n8\n\nkey_2\n2\n8\n\nkey_3\n3\n8\n\nkey_4\n4\n8\n\nkey_5\n5\n8\n\nkey_6\n6\n8\n\nkey_7\n7\n8\n\nkey_8\n8\n8\n\nkey_9\n9\n8\n\nkey_0\n1\n7\n\nkey_1\n1\n7\n\nkey_2\n2\n7\n\nkey_3\n3\n7\n\nkey_4\n4\n7\n\nkey_5\n5\n7\n\nkey_6\n6\n7\n\nkey_7\n7\n7\n\nkey_8\n8\n7\n\nkey_9\n9\n7\n\nkey_0\n1\n6\n\nkey_1\n1\n6\n\nkey_2\n2\n6\n\nkey_3\n3\n6\n\nkey_4\n4\n6\n\nkey_5\n5\n6\n\nkey_6\n6\n6\n\nkey_7\n7\n6\n\nkey_8\n8\n6\n\nkey_9\n9\n6\n\nkey_0\n1\n5\n\nkey_1\n1\n5\n\nkey_2\n2\n5\n\nkey_3\n3\n5\n\nkey_4\n4\n5\n\nkey_5\n5\n5\n\nkey_6\n6\n5\n\nkey_7\n7\n5\n\nkey_8\n8\n5\n\nkey_9\n9\n5\n\nkey_0\n1\n4\n\nkey_1\n1\n4\n\nkey_2\n2\n4\n\nkey_3\n3\n4\n\nkey_4\n4\n4\n\nkey_5\n5\n4\n\nkey_6\n6\n4\n\nkey_7\n7\n4\n\nkey_8\n8\n4\n\nkey_9\n9\n4\n\nkey_0\n1\n3\n\nkey_1\n1\n3\n\nkey_2\n2\n3\n\nkey_3\n3\n3\n\nkey_4\n4\n3\n\nkey_5\n5\n3\n\nkey_6\n6\n3\n\nkey_7\n7\n3\n\nkey_8\n8\n3\n\nkey_9\n9\n3\n\nkey_0\n1\n2\n\nkey_1\n1\n2\n\nkey_2\n2\n2\n\nkey_3\n3\n2\n\nkey_4\n4\n2\n\nkey_5\n5\n2\n\nkey_6\n6\n2\n\nkey_7\n7\n2\n\nkey_8\n8\n2\n\nkey_9\n9\n2\n\nkey_0\n1\n1\n\nkey_1\n1\n1\n\nkey_2\n2\n1\n\nkey_3\n3\n1\n\nkey_4\n4\n1\n\nkey_5\n5\n1\n\nkey_6\n6\n1\n\nkey_7\n7\n1\n\nkey_8\n8\n1\n\nkey_9\n9\n1\n\nJOIN ON + ORDER BY\nkey_9\n9\n1\n\nkey_8\n8\n2\n\nkey_7\n7\n3\n\nkey_6\n6\n4\n\nkey_5\n5\n5\n\nkey_4\n4\n6\n\nkey_3\n3\n7\n\nkey_2\n2\n8\n\nkey_0\n1\n9\n\nkey_1\n1\n9\n\nJOIN ON + WHERE + ORDER BY\nkey_9\n9\n1\n\nkey_8\n8\n2\n\nkey_7\n7\n3\n\nkey_6\n6\n4\n\nJOIN ON + GROUP BY + ORDER BY\n9\n1\n\n8\n2\n\n7\n3\n\n6\n4\n\n5\n5\n\n4\n6\n\n3\n7\n\n2\n8\n\n1\n9\n\nJOIN ON + GROUP BY + HAVING + ORDER BY\n9\n1\n\n1\n9\n\n"
		"nestedSql.txt"  || "key_0\n1\n9\n\nkey_1\n1\n9\n\nkey_2\n2\n8\n\nkey_3\n3\n7\n\nkey_4\n4\n6\n\nkey_5\n5\n5\n\nkey_6\n6\n4\n\nkey_7\n7\n3\n\nkey_8\n8\n2\n\nkey_9\n9\n1\n\n"
	}
}
