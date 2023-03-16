package org.example.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.example.lexer.error.InconsistentNewLine;
import org.example.token.Position;

public class PositionalReaderImpl implements PositionalReader {

	private static final int NEW_LINE_CODE = '\n';
	private static final int CARRIAGE_RETURN_CODE = '\r';

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
		int nextCharacter;

		if (currentCharacter == NEW_LINE_CODE) {
			reader.mark(1);
			nextCharacter = reader.read();
			if (nextCharacter == CARRIAGE_RETURN_CODE) {
				validateNewLine("\n\r");
			} else {
				validateNewLine("\n");
				reader.reset();
			}
			advanceLine();
		} else if (currentCharacter == CARRIAGE_RETURN_CODE) {
			reader.mark(1);
			nextCharacter = reader.read();
			if (nextCharacter == NEW_LINE_CODE) {
				currentCharacter = NEW_LINE_CODE;
				advanceLine();
				validateNewLine("\r\n");
			} else {
				reader.reset();
			}
		}
		return currentCharacter;
	}

	private void validateNewLine(String detectedNewLine) {
		if (newLineCharacter == null) {
			newLineCharacter = detectedNewLine;
		} else if (!StringUtils.equals(newLineCharacter, detectedNewLine)) {
			throw new InconsistentNewLine(detectedNewLine, newLineCharacter);
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
