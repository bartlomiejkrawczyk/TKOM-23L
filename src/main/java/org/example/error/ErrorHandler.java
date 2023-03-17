package org.example.error;

public interface ErrorHandler {

	void handleLexerException(Exception exception);

	void handleParserException(Exception exception);

	void handleSemanticAnalysisException(Exception exception);
}
