package org.example.error;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorHandlerImpl implements ErrorHandler {

	@Override
	public void handleLexerException(Exception exception) {
		log.error(exception.getMessage());
	}
}
