package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.example.ast.Expression;
import org.example.error.ErrorHandler;
import org.example.error.ErrorHandlerImpl;
import org.example.error.TooManyExceptionsException;
import org.example.lexer.CommentFilterLexer;
import org.example.lexer.Lexer;
import org.example.lexer.LexerImpl;
import org.example.parser.Parser;
import org.example.parser.ParserImpl;
import org.example.token.Token;
import org.example.token.TokenType;

@Slf4j
public class Main {

	public static void main(String[] args) {
		if (args.length > 0) {
			var file = args[0];
			run(file);
		}
	}

	private static void run(String fileName) {
		var file = new File(fileName);
		var errorHandler = new ErrorHandlerImpl();
		try (var inputStream = new FileInputStream(file)) {
			handleStream(inputStream, errorHandler);
		} catch (IOException e) {
			log.error("IOException: Cannot read input file", e);
		} catch (TooManyExceptionsException e) {
			log.error("TooManyExceptions: {}", e.getMessage());
		}

		try (var inputStream = new FileInputStream(file)) {
			errorHandler.showExceptions(new InputStreamReader(inputStream));
		} catch (IOException e) {
			log.error("IOException: Cannot read input file", e);
		}
	}


	@SuppressWarnings({"unused", "java:S125"})
	private static void handleStream(InputStream file, ErrorHandler errorHandler) {
		var reader = new InputStreamReader(file);
		var lexer = new LexerImpl(reader, errorHandler);
		var filter = new CommentFilterLexer(lexer);
		var parser = new ParserImpl(filter, errorHandler);

		testLexer(lexer);
		// testParser(parser);
	}

	@SuppressWarnings("unused")
	private static void testLexer(Lexer lexer) {
		Token token;
		do {
			token = lexer.nextToken();
			log.info("Token = {}", token);
		} while (token.getType() != TokenType.END_OF_FILE);
	}

	@SuppressWarnings("unused")
	private static void testParser(Parser parser) {
		Expression expression;
		do {
			expression = parser.nextExpression();
			log.info("Expression = {}", expression);
		} while (expression != null);
	}

}
