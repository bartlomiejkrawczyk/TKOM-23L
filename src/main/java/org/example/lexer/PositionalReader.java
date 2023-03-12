package org.example.lexer;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import lombok.Getter;
import org.example.token.Position;

public class PositionalReader extends Reader {

	private final LineNumberReader reader;
	@Getter
	private Position position;
	private int previousLine;

	public PositionalReader(Reader reader) {
		this.reader = new LineNumberReader(reader);
		this.position = new Position();
		this.previousLine = 0;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		var value = reader.read(cbuf, off, len);
		updatePosition();
		return value;
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	private void updatePosition() {
		var newLine = reader.getLineNumber();
		if (newLine != previousLine) {
			position = position.nextLine();
		} else {
			position = position.nextCharacter();
		}
		previousLine = newLine;
	}
}
