package org.example.token;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TokenType {
	END_OF_FILE,

	FUNCTION_DEFINITION("fun"),
	RETURN("return"),
	WHILE("while"),
	IF("if"),
	ELSE("else"),

	OPEN_CURLY_PARENTHESES("{"),
	OPEN_ROUND_PARENTHESES("("),
	CLOSED_CURLY_PARENTHESES("}"),
	CLOSED_ROUND_PARENTHESES(")"),

	SEMICOLON(";"),
	COLON(":"),
	COMMA(","),

	AND("and"),
	NOT("not"),
	OR("or"),

	EQUALITY("=="),
	INEQUALITY("!="),
	GREATER(">"),
	LESS("<"),
	GREATER_EQUAL(">="),
	LESS_EQUAL("<="),

	EQUALS("="),

	PLUS("+"),
	MINUS("-"),
	TIMES("*"),
	DIVIDE("/"),

	SINGLE_LINE_COMMENT("//", "\n"),
	MULTI_LINE_COMMENT("/*", "*/"),

	IDENTIFIER,
	INTEGER_CONSTANT,
	FLOATING_POINT_CONSTANT,
	STRING_DOUBLE_QUOTE_CONSTANT("\"", "\""),
	STRING_SINGLE_QUOTE_CONSTANT("'", "'"),
	;

	TokenType(String keyword) {
		this.keyword = keyword;
	}

	private String keyword;
	private String enclosingKeyword;
}
