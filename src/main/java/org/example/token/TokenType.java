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

	SELECT("select", false),
	FROM("from", false),
	JOIN("join", false),
	ON("on", false),
	WHERE("where", false),
	GROUP("group", false),
	HAVING("having", false),
	ORDER("order", false),
	BY("by", false),
	ASCENDING("asc", false),
	DESCENDING("desc", false),

	AS("as", false),

	OPEN_CURLY_PARENTHESES("{"),
	OPEN_ROUND_PARENTHESES("("),
	OPEN_SQUARE_PARENTHESES("["),
	CLOSED_CURLY_PARENTHESES("}"),
	CLOSED_ROUND_PARENTHESES(")"),
	CLOSED_SQUARE_PARENTHESES("]"),
	VERTICAL_BAR_PARENTHESES("|"),

	SEMICOLON(";"),
	COLON(":"),
	COMMA(","),
	DOT("."),
	EXPLICIT_CAST("@"),

	AND("and", false),
	NOT("not", false),
	OR("or", false),

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

	SINGLE_LINE_COMMENT("#", "\n"),
	MULTI_LINE_COMMENT("/*", "*/"),

	IDENTIFIER,
	INTEGER_CONSTANT,
	FLOATING_POINT_CONSTANT,
	STRING_DOUBLE_QUOTE_CONSTANT("\"", "\""),
	STRING_SINGLE_QUOTE_CONSTANT("'", "'"),

	BOOLEAN_TRUE("true"),
	BOOLEAN_FALSE("false"),

	INT("int"),
	DOUBLE("double"),
	BOOLEAN("boolean"),
	VOID("void"),

	STRING("String"),

	MAP("Map"),
	ITERABLE("Iterable"),
	TUPLE("Tuple"),
	COMPARATOR("Comparator"),
	;

	TokenType(String keyword) {
		this.keyword = keyword;
	}

	TokenType(String keyword, String enclosingKeyword) {
		this.keyword = keyword;
		this.enclosingKeyword = enclosingKeyword;
	}

	TokenType(String keyword, boolean caseSensitive) {
		this.keyword = keyword;
		this.caseSensitive = caseSensitive;
	}

	private String keyword;
	private String enclosingKeyword;
	private boolean caseSensitive = true;
}
