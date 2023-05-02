package org.example.parser;

import org.example.ast.Program;
import org.example.parser.error.CriticalParserException;

public interface Parser {

	Program parseProgram() throws CriticalParserException;
}
