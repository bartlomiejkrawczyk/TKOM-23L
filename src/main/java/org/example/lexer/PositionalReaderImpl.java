package org.example.lexer;

import io.vavr.API;
import io.vavr.Function0;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.example.lexer.error.InconsistentNewLine;
import org.example.token.Position;

public class PositionalReaderImpl implements PositionalReader {

	private static final int NEW_LINE_CODE = '\n';
	private static final int CARRIAGE_RETURN_CODE = '\r';
	private static final String NEW_LINE = "\n";
	private static final String NEW_LINE_CARRIAGE_RETURN = "\n\r";
	private static final String CARRIAGE_RETURN_NEW_LINE = "\r\n";

	private final Map<Integer, Function0<Integer>> newLineHandlers = Map.of(
			NEW_LINE_CODE, API.unchecked(this::handleNewLine),
			CARRIAGE_RETURN_CODE, API.unchecked(this::handleCarriageReturn)
	);

	private final BufferedReader reader;
	private String newLineCharacter;

	@Getter
	private Position position;

	public PositionalReaderImpl(Reader reader) {
		this.reader = toBufferedReader(reader);
		this.position = new Position();
		this.newLineCharacter = null;
	}

	@Override
	public int read() throws IOException {
		var currentCharacter = reader.read();
		advanceCharacter();
		return newLineHandlers.getOrDefault(
				currentCharacter,
				() -> currentCharacter
		).apply();
	}

	@Override
	public int getLine() {
		return position.getLine();
	}

	private int handleNewLine() throws IOException {
		reader.mark(1);
		var nextCharacter = reader.read();
		if (nextCharacter == CARRIAGE_RETURN_CODE) {
			validateNewLine(NEW_LINE_CARRIAGE_RETURN);
		} else {
			validateNewLine(NEW_LINE);
			reader.reset();
		}
		advanceLine();
		return NEW_LINE_CODE;
	}

	private int handleCarriageReturn() throws IOException {
		reader.mark(1);
		var nextCharacter = reader.read();
		if (nextCharacter == NEW_LINE_CODE) {
			advanceLine();
			validateNewLine(CARRIAGE_RETURN_NEW_LINE);
			return NEW_LINE_CODE;
		} else {
			reader.reset();
			return CARRIAGE_RETURN_CODE;
		}
	}

	private void validateNewLine(String detectedNewLine) {
		if (newLineCharacter == null) {
			newLineCharacter = detectedNewLine;
		} else if (!StringUtils.equals(newLineCharacter, detectedNewLine)) {
			throw new InconsistentNewLine(detectedNewLine, newLineCharacter, position);
		}
	}

	private void advanceLine() {
		position = position.nextLine();
	}

	private void advanceCharacter() {
		position = position.nextCharacter();
	}

	private BufferedReader toBufferedReader(Reader abstractReader) {
		if (abstractReader instanceof BufferedReader bufferedReader) {
			return bufferedReader;
		} else {
			return new BufferedReader(abstractReader, 8);
		}
	}
}
