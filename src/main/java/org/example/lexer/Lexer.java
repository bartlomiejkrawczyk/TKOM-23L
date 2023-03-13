package org.example.lexer;

import java.io.Closeable;
import org.example.token.Token;

public interface Lexer extends Closeable {

	Token nextToken();
}
