package org.example.error;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.interpreter.error.InterpreterException;
import org.example.lexer.CharactersUtility;
import org.example.lexer.LexerConfiguration;
import org.example.lexer.PositionalReader;
import org.example.lexer.PositionalReaderImpl;
import org.example.lexer.error.LexerException;
import org.example.parser.error.ParserException;
import org.example.token.Position;

@ToString
@Slf4j
public class ErrorHandlerImpl implements ErrorHandler {

	private final Map<Position, Set<PositionalException>> exceptions = new HashMap<>();

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public void showExceptions(Reader reader) throws IOException {
		var positionalReader = new PositionalReaderImpl(reader);
		while (processLine(positionalReader)) ;
		exceptions.clear();
	}

	private boolean processLine(PositionalReader reader) throws IOException {
		var lineBuilder = formatLine(reader);
		var exceptionsOnLine = new ArrayList<PositionalException>();
		while (true) {
			var read = reader.read();
			if (read == CharactersUtility.END_OF_FILE) {
				logLine(lineBuilder.toString(), exceptionsOnLine);
				return false;
			}
			var character = Character.toString(read);
			if (StringUtils.equals(character, CharactersUtility.NEW_LINE)) {
				logLine(lineBuilder.toString(), exceptionsOnLine);
				return true;
			} else if (lineBuilder.length() < LexerConfiguration.MAX_STRING_LENGTH) {
				lineBuilder.append(character);
			}

			var currentPosition = reader.getPosition();
			var exceptionsAtPosition = exceptions.getOrDefault(currentPosition, Set.of());
			exceptionsOnLine.addAll(exceptionsAtPosition);
		}
	}

	private void logLine(String line, List<PositionalException> exceptionsOnLine) {
		if (!exceptionsOnLine.isEmpty()) {
			log.info(line);
			for (var exception : exceptionsOnLine) {
				log.error(exception.getMessage());
			}
		}
	}

	private StringBuilder formatLine(PositionalReader reader) {
		var line = String.format("%4d: ", reader.getLine());
		return new StringBuilder(line);
	}

	@Override
	public void handleLexerException(LexerException exception) {
		handleException(exception);
	}

	@Override
	public void handleParserException(ParserException exception) {
		handleException(exception);
	}

	@Override
	public void handleInterpreterException(InterpreterException exception) {
		handleException(exception);
	}

	private void handleException(PositionalException exception) {
		var exceptionsAtPosition = exceptions.computeIfAbsent(exception.getPosition(), position -> new HashSet<>());
		exceptionsAtPosition.add(exception);
		if (exceptions.size() > ErrorHandlerConfiguration.MAX_ERRORS) {
			throw new TooManyExceptionsException();
		}
	}
}
