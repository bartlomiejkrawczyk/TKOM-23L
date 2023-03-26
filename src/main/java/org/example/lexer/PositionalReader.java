package org.example.lexer;

import java.io.IOException;
import org.example.token.Position;

public interface PositionalReader {

	Position getPosition();

	int getLine();

	int read() throws IOException;
}
