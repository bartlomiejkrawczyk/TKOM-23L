package org.example.error;

import java.io.IOException;
import java.io.Reader;
import org.example.interpreter.error.InterpreterException;
import org.example.lexer.error.LexerException;
import org.example.parser.error.ParserException;

public interface ErrorHandler {

	void handleLexerException(LexerException exception);

	void handleParserException(ParserException exception);

	void handleInterpreterException(InterpreterException exception);

	void showExceptions(Reader reader) throws IOException;
}
