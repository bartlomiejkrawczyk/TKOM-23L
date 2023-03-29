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

	AND("and", 5),
	NOT("not"),
	OR("or", 4),

	EQUALITY("==", 10),
	INEQUALITY("!=", 10),
	GREATER(">", 10),
	LESS("<", 10),
	GREATER_EQUAL(">=", 10),
	LESS_EQUAL("<=", 10),

	EQUALS("="), // RIGHT ORDER

	PLUS("+", 20), // LEFT ORDER
	MINUS("-", 20),
	TIMES("*", 40),
	DIVIDE("/", 40),

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

	TokenType(String keyword, int precedence) {
		this.keyword = keyword;
		this.precedence = precedence;
	}

	private String keyword;
	private String enclosingKeyword;
	private int precedence = -1;
}
