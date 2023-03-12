package org.example;

import java.io.InputStreamReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.lexer.LexerImpl;
import org.example.token.Token;
import org.example.token.TokenType;

@Slf4j
public class Main {

	@SneakyThrows
	public static void main(String[] args) {
		var file = Main.class.getResourceAsStream("/test.txt");
		assert file != null;
		var reader = new InputStreamReader(file);
		var lexer = new LexerImpl(reader);

		Token token;
		do {
			token = lexer.nextToken();
			log.info("Token = {}", token);
		} while (token.getType() != TokenType.END_OF_FILE);

		lexer.close();
	}

}
