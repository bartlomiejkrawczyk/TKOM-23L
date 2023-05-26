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
import org.example.interpreter.InterpretingVisitor;
import org.example.lexer.CommentFilterLexer;
import org.example.lexer.LexerImpl;
import org.example.parser.ParserImpl;
import org.example.parser.error.CriticalParserException;

@Slf4j
public class Main {

	private static final String IO_EXCEPTION_MESSAGE = "IOException: Cannot read input file: {}";

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
			log.error(IO_EXCEPTION_MESSAGE, e.getMessage());
			return;
		} catch (TooManyExceptionsException e) {
			log.error("TooManyExceptions: {}", e.getMessage());
		} catch (CriticalParserException exception) {
			log.error("Parsing stopped");
		}

		try (var inputStream = new FileInputStream(file)) {
			errorHandler.showExceptions(new InputStreamReader(inputStream));
		} catch (IOException e) {
			log.error(IO_EXCEPTION_MESSAGE, e.getMessage());
		}

		if (program == null) {
			log.error("Could not start the program!");
			return;
		}

		runProgram(program, errorHandler);

		try (var inputStream = new FileInputStream(file)) {
			errorHandler.showExceptions(new InputStreamReader(inputStream));
		} catch (IOException e) {
			log.error(IO_EXCEPTION_MESSAGE, e.getMessage());
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

	@SuppressWarnings("java:S106")
	private static void runProgram(Program program, ErrorHandler errorHandler) {
		var interpreter = new InterpretingVisitor(errorHandler, System.out, program);
		interpreter.execute();
	}
}
