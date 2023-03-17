package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.example.error.ErrorHandlerImpl;
import org.example.lexer.CommentFilterLexer;
import org.example.lexer.LexerImpl;
import org.example.token.Token;
import org.example.token.TokenType;

@Slf4j
public class Main {

	public static void main(String[] args) {
		var resourceName = "/test.txt";
		run(resourceName);
	}

	private static void run(String resourceName) {
		try (var file = Main.class.getResourceAsStream(resourceName)) {
			handleStream(file);
		} catch (IOException e) {
			log.error("IOException: Cannot read input file", e);
		}
	}

	private static void handleStream(InputStream file) {
		var reader = new InputStreamReader(file);
		var errorHandler = new ErrorHandlerImpl();
		var lexer = new LexerImpl(reader, errorHandler);
		var filter = new CommentFilterLexer(lexer);

		Token token;
		do {
			token = filter.nextToken();
			log.info("Token = {}", token);
		} while (token.getType() != TokenType.END_OF_FILE);
	}

}
