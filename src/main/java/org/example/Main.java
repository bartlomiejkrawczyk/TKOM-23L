package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.example.ast.Program;
import org.example.error.ErrorHandler;
import org.example.error.ErrorHandlerImpl;
import org.example.error.TooManyExceptionsException;
import org.example.interpreter.Interpreter;
import org.example.interpreter.InterpretingVisitor;
import org.example.interpreter.error.CriticalInterpreterException;
import org.example.lexer.CommentFilterLexer;
import org.example.lexer.Lexer;
import org.example.lexer.LexerImpl;
import org.example.parser.Parser;
import org.example.parser.ParserImpl;
import org.example.parser.error.CriticalParserException;
import org.example.token.Token;
import org.example.token.TokenType;
import org.example.visitor.PrintingVisitor;

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
		Program program = null;
		try (var inputStream = new FileInputStream(file)) {
			program = handleStream(inputStream, errorHandler);
		} catch (IOException e) {
			log.error("IOException: Cannot read input file: {}", e.getMessage());
			return;
		} catch (TooManyExceptionsException e) {
			log.error("TooManyExceptions: {}", e.getMessage());
		} catch (CriticalParserException exception) {
			log.error("CriticalParserException", exception);
		}

		try (var inputStream = new FileInputStream(file)) {
			errorHandler.showExceptions(new InputStreamReader(inputStream));
		} catch (IOException e) {
			log.error("IOException: Cannot read input file: {}", e.getMessage());
		}

		if (program == null) {
			log.error("Could not start the program!");
			return;
		}

		try {
			runProgram(program, errorHandler);
		} catch (CriticalInterpreterException exception) {
			log.error("CriticalInterpreterException", exception);
		}

		try (var inputStream = new FileInputStream(file)) {
			errorHandler.showExceptions(new InputStreamReader(inputStream));
		} catch (IOException e) {
			log.error("IOException: Cannot read input file: {}", e.getMessage());
		}
	}


	@SuppressWarnings({"unused", "java:S125"})
	private static Program handleStream(InputStream file, ErrorHandler errorHandler) {
		var reader = new InputStreamReader(file);
		var lexer = new LexerImpl(reader, errorHandler);
		var filter = new CommentFilterLexer(lexer);
		var parser = new ParserImpl(filter, errorHandler);
		return parser.parseProgram();
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
		var program = parser.parseProgram();
		var visitor = new PrintingVisitor();
		visitor.visit(program);
		log.info(visitor.print());
	}

	@SuppressWarnings("unused")
	private static void runProgram(Program program, ErrorHandler errorHandler) {
		var interpreter = new InterpretingVisitor(program, errorHandler);
		testInterpreter(interpreter);
	}

	@SuppressWarnings("unused")
	private static void testInterpreter(Interpreter interpreter) {
		interpreter.execute();
	}
}
