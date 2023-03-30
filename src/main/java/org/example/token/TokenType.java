package org.example.token;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum TokenType {
	END_OF_FILE,

	FUNCTION_DEFINITION("fun"),
	RETURN("return"),
	WHILE("while"),
	FOR("for"),
	IF("if"),
	ELSE("else"),

	SELECT("select"),
	FROM("from"),
	JOIN("join"),
	ON("on"),
	WHERE("where"),
	GROUP("group"),
	HAVING("having"),
	ORDER("order"),
	BY("by"),
	ASCENDING("ascending"),
	DESCENDING("descending"),

	AS("as"),

	OPEN_CURLY_PARENTHESES("{"),
	OPEN_ROUND_PARENTHESES("("),
	OPEN_SQUARE_PARENTHESES("["),
	CLOSED_CURLY_PARENTHESES("}"),
	CLOSED_ROUND_PARENTHESES(")"),
	CLOSED_SQUARE_PARENTHESES("]"),

	SEMICOLON(";"),
	COLON(":"),
	COMMA(","),
	DOT("."),

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
	BOOLEAN_CONSTANT,
	STRING_DOUBLE_QUOTE_CONSTANT("\"", "\""),
	STRING_SINGLE_QUOTE_CONSTANT("'", "'"),
	;

	TokenType(String keyword) {
		this.keyword = keyword;
	}

	TokenType(String keyword, String enclosingKeyword) {
		this.keyword = keyword;
		this.enclosingKeyword = enclosingKeyword;
	}

	private String keyword;
	private String enclosingKeyword;
}
